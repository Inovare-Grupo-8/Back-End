package org.com.imaapi.service.impl;

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
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
public class GoogleTokenServiceImpl implements GoogleTokenService {

    private final OauthTokenRepository oauthTokenRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientManager oauthClientManager;

    public GoogleTokenServiceImpl(OauthTokenRepository oauthTokenRepository,
                                  ClientRegistrationRepository clientRegistrationRepository,
                                  OAuth2AuthorizedClientManager oauthClientManager) {
        this.oauthTokenRepository = oauthTokenRepository;
        this.oauthClientManager = oauthClientManager;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public void salvarToken(Usuario usuario, OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken) {
        OauthToken oauthToken = oauthTokenRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .orElse(new OauthToken());

        oauthToken.setUsuario(usuario);

        oauthToken.atualizarTokens(
                accessToken,
                refreshToken
        );

        oauthTokenRepository.save(oauthToken);
    }

    @Override
    public boolean tokenExpirou(OauthToken token) {
        return token.getAccessTokenExpiresAt() == null ||
                token.getAccessTokenExpiresAt().isBefore(Instant.now().minusSeconds(30));
    }

    @Override
    public void renovarAccessToken(Authentication authentication) {
        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
                .withClientRegistrationId("google")
                .principal(authentication)
                .build();

        OAuth2AuthorizedClient client = oauthClientManager.authorize(request);

        if (client == null || client.getAccessToken() == null) {
            throw new IllegalStateException("Não foi possível renovar o token de acesso do Google");
        }
    }

    @Override
    public boolean contemEscoposNecessarios(Integer idUsuario, Set<String> escopos) {
        OauthToken token = oauthTokenRepository.findByUsuarioIdUsuario(idUsuario)
                .orElseThrow(() -> new IllegalStateException("Token não encontrado para o usuário"));

        if (tokenExpirou(token)) {
            renovarAccessToken(SecurityContextHolder.getContext().getAuthentication());
            token = oauthTokenRepository.findByUsuarioIdUsuario(idUsuario)
                    .orElseThrow(() -> new IllegalStateException("Token não encontrado para o usuário"));
        }

        return token.getAccessTokenObject().getScopes().containsAll(escopos);
    }

    @Override
    public boolean contemEscoposComClienteOAuth(Authentication authentication, Set<String> escoposNecessarios) {
        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
                .withClientRegistrationId("google")
                .principal(authentication)
                .build();

        OAuth2AuthorizedClient client = oauthClientManager.authorize(request);
        if (client == null || client.getAccessToken() == null) {
            throw new IllegalStateException("Não foi possível obter o OAuth2AuthorizedClient");
        }

        return client.getAccessToken().getScopes().containsAll(escoposNecessarios);
    }

    @Override
    public String construirUrlIncremental(Set<String> escoposAdicionais) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

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
            throw new IllegalStateException("Authentication não é do tipo AppUserAuthenticationToken");
        }

        ClientRegistration registration = appToken.getAuthorizedClient().getClientRegistration();

        OAuth2AuthorizationRequest authRequest = OAuth2AuthorizationRequest.authorizationCode()
                .clientId(registration.getClientId())
                .authorizationUri(registration.getProviderDetails().getAuthorizationUri())
                .redirectUri("http://localhost:3030/")
                .scopes(appToken.getAuthorizedClient().getAccessToken().getScopes())
                .state(UUID.randomUUID().toString())
                .build();

        OAuth2AuthorizedClient client = oauthClientManager.authorize(
                OAuth2AuthorizeRequest.withClientRegistrationId(registration.getRegistrationId())
                        .principal(authentication)
                        .attribute("code", code)
                        .build()
        );

        if (client == null || client.getAccessToken() == null) {
            throw new IllegalStateException("Não foi possível trocar o código por token");
        }

        // Salva no banco
        salvarToken((Usuario) authentication.getPrincipal(),
                client.getAccessToken(),
                client.getRefreshToken());

        return client;
    }
}
