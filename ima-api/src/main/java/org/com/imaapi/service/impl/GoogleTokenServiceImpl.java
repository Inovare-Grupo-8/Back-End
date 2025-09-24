package org.com.imaapi.service.impl;

import jakarta.servlet.ServletException;
import org.com.imaapi.config.oauth2.AppUserAuthenticationToken;
import org.com.imaapi.model.oauth.OauthToken;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.repository.OauthTokenRepository;
import org.com.imaapi.service.GoogleTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
public class GoogleTokenServiceImpl implements GoogleTokenService {

    private final OauthTokenRepository oauthTokenRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final OAuth2AuthorizedClientManager oauthClientManager;

    public GoogleTokenServiceImpl(OauthTokenRepository oauthTokenRepository,
                                  OAuth2AuthorizedClientService authorizedClientService,
                                  OAuth2AuthorizedClientManager oauthClientManager) {
        this.oauthTokenRepository = oauthTokenRepository;
        this.oauthClientManager = oauthClientManager;
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public void salvarAccessToken(Usuario usuario, OAuth2AccessToken accessToken) {
        OauthToken oauthToken = oauthTokenRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .orElse(new OauthToken());

        oauthToken.atualizarAccessToken(accessToken);
        oauthTokenRepository.save(oauthToken);
    }

    @Override
    public boolean possuiRefreshToken(Integer idUsuario) {
        return oauthTokenRepository.existsByUsuarioIdUsuario(idUsuario);
    }

    @Override
    public void salvarRefreshToken(Usuario usuario, OAuth2RefreshToken refreshToken) {
        oauthTokenRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        OauthToken oauthToken = oauthTokenRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new IllegalStateException("Usuário não possui tokens salvos"));

        oauthToken.atualizarRefreshToken(refreshToken);
        oauthTokenRepository.save(oauthToken);
    }

    @Override
    public boolean tokenEstaParaExpirar(OAuth2AccessToken token) {
        return token.getExpiresAt() == null ||
                token.getExpiresAt().isBefore(Instant.now().minusSeconds(30));
    }

    @Override
    public void renovarAccessToken(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();

        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
                .withClientRegistrationId("google")
                .principal(authentication)
                .build();

        OAuth2AuthorizedClient client;
        try {
            client = oauthClientManager.authorize(request);
            atualizarAuthorizedClient(authentication, client);
        } catch (OAuth2AuthorizationException e) {
            if ("invalid_grant".equals(e.getError().getErrorCode())) {
                removerTokens(usuario);
                throw new IllegalStateException("Refresh token revogado. Usuário precisa logar novamente.");
            }
            throw e;
        }

        if (client == null || client.getAccessToken() == null) {
            throw new IllegalStateException("Não foi possível renovar o token de acesso do Google");
        }

        salvarAccessToken(usuario, client.getAccessToken());

        boolean usuarioPossuiRefreshToken = oauthTokenRepository.existsByUsuarioIdUsuario(usuario.getIdUsuario());
        if (client.getRefreshToken() != null && !usuarioPossuiRefreshToken) {
            salvarRefreshToken(usuario, client.getRefreshToken());
        }
    }

    @Override
    public boolean contemEscoposNecessarios(Integer idUsuario, Set<String> escopos, Authentication authentication) {
        OauthToken token = oauthTokenRepository.findByUsuarioIdUsuario(idUsuario)
                .orElseThrow(() -> new IllegalStateException("Token não encontrado para o usuário"));

        OAuth2AccessToken accessToken = token.getAccessTokenObject();
        if (tokenEstaParaExpirar(accessToken)) {
            renovarAccessToken(authentication);
        }

        return accessToken.getScopes().containsAll(escopos);
    }

    @Override
    public boolean contemEscoposComClienteOAuth(Authentication authentication, Set<String> escoposNecessarios) {
        if (!(authentication instanceof AppUserAuthenticationToken token)) {
            return false;
        }

        OAuth2AuthorizedClient client = token.getAuthorizedClient();
        if (client == null || client.getAccessToken() == null) {
            return false;
        }

        OAuth2AccessToken accessToken = client.getAccessToken();

        // Se token estiver prestes a expirar, renova antes de checar escopos
        if (tokenEstaParaExpirar(accessToken)) {
            renovarAccessToken(authentication);
            client = token.getAuthorizedClient();
            accessToken = client.getAccessToken();
        }

        return accessToken.getScopes().containsAll(escoposNecessarios);
    }

    @Override
    public String construirUrlIncremental(Set<String> escoposAdicionais, Authentication authentication) {
        OAuth2AuthorizedClient client = null;
        if (authentication instanceof AppUserAuthenticationToken appToken) {
            client = appToken.getAuthorizedClient();
        } else {
            System.out.println("Authentication não é do tipo AppUserAuthenticationToken");
        }

        if (client == null) {
            throw new IllegalStateException("Não foi possível obter o OAuth2AuthorizedClient");
        }

        return UriComponentsBuilder
                .fromUriString(client.getClientRegistration().getProviderDetails().getAuthorizationUri())
                .queryParam("client_id", client.getClientRegistration().getClientId())
                .queryParam("redirect_uri", "http://localhost:8080/oauth2/googlecallback")
                .queryParam("response_type", "code")
                .queryParam("scope", String.join(" ", escoposAdicionais))
                .queryParam("include_granted_scopes", "true")
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .build()
                .toUriString();
    }

    @Override
    public OAuth2AuthorizedClient trocarCodePorToken(String code) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AppUserAuthenticationToken appToken)) {
            throw new IllegalStateException("Usuário não autenticado pelo google ou token inválido");
        }

        ClientRegistration registration = appToken.getAuthorizedClient().getClientRegistration();

        String state = "troca-token-" + UUID.randomUUID();

        OAuth2AuthorizationRequest authRequest = OAuth2AuthorizationRequest.authorizationCode()
                .clientId(registration.getClientId())
                .authorizationUri(registration.getProviderDetails().getAuthorizationUri())
                .redirectUri("http://localhost:8080/oauth2/googlecallback")
                .state(state)
                .build();

        OAuth2AuthorizationCodeGrantRequest grantRequest =
                new OAuth2AuthorizationCodeGrantRequest(
                        registration,
                        new OAuth2AuthorizationExchange(
                            authRequest,
                            OAuth2AuthorizationResponse.success(code)
                                .redirectUri("http://localhost:8080/oauth2/googlecallback")
                                .state(state)
                                .build()
                ));

        RestClientAuthorizationCodeTokenResponseClient tokenResponseClient = new RestClientAuthorizationCodeTokenResponseClient();
        OAuth2AccessTokenResponse tokenResponse = tokenResponseClient.getTokenResponse(grantRequest);

        OAuth2AuthorizedClient client = new OAuth2AuthorizedClient(
                registration,
                authentication.getName(),
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken()
        );

        authorizedClientService.saveAuthorizedClient(client, authentication);

        System.out.println("Token Response: " + tokenResponse);
        System.out.println("Access Token (client): " + client.getAccessToken());

        Usuario usuario = (Usuario) authentication.getPrincipal();
        OAuth2AccessToken accessToken = client.getAccessToken();

        this.salvarAccessToken(usuario, accessToken);
        return client;
    }

    @Override
    public void removerTokens(Usuario usuario) {
        oauthTokenRepository.deleteByUsuarioIdUsuario(usuario.getIdUsuario());
    }

    @Override
    public void processarTokenOAuth2(Usuario usuario, OAuth2AuthorizedClient authorizedClient, Authentication authentication) throws ServletException {
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

        salvarAccessToken(usuario, accessToken);
        if (refreshToken != null) {
            salvarRefreshToken(usuario, refreshToken);
        }

        if (tokenEstaParaExpirar(accessToken)) {
            renovarAccessToken(authentication);
        }
    }

    private void atualizarAuthorizedClient(Authentication authentication, OAuth2AuthorizedClient client) {
        AppUserAuthenticationToken authenticationToken = new AppUserAuthenticationToken(
                authentication.getPrincipal(),
                authentication.getAuthorities(),
                "google",
                client
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
