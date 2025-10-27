package org.com.imaapi.service.impl;

import org.com.imaapi.exception.MissingScopeException;
import org.com.imaapi.service.GoogleTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
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

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final OAuth2AuthorizedClientManager clientManager;
    private final String clientRegistrationId = "google";

    public GoogleTokenServiceImpl(OAuth2AuthorizedClientService authorizedClientService,
                                  OAuth2AuthorizedClientManager clientManager) {
        this.clientManager = clientManager;
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public boolean tokenEstaParaExpirar(OAuth2AccessToken token) {
        return token.getExpiresAt() == null ||
                token.getExpiresAt().isBefore(Instant.now().minusSeconds(30));
    }

    @Override
    public boolean contemEscoposNecessarios(Set<String> escopos, Authentication authentication) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(clientRegistrationId, authentication.getName());

        if (client == null || client.getAccessToken() == null) {
            return false;
        }

        OAuth2AccessToken accessToken = client.getAccessToken();

        if (tokenEstaParaExpirar(accessToken)) {
            client = obterClienteAutorizado(authentication);
            accessToken = client.getAccessToken();
        }

        Set<String> escoposRecebidos = accessToken.getScopes();
        return escoposRecebidos != null && accessToken.getScopes().containsAll(escopos);
    }

    @Override
    public OAuth2AuthorizedClient obterClienteAutorizado(Authentication authentication) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(clientRegistrationId)
                .principal(authentication)
                .build();

        OAuth2AuthorizedClient clientAtualizado = clientManager.authorize(authorizeRequest);
        if (clientAtualizado == null) {
            throw new OAuth2AuthenticationException("Falha ao renovar o token de acesso");
        }

        authorizedClientService.saveAuthorizedClient(clientAtualizado, authentication);
        return clientAtualizado;
    }

    @Override
    public String obterAccessToken(Authentication authentication) {
        return obterClienteAutorizado(authentication)
                .getAccessToken()
                .getTokenValue();
    }

    @Override
    public OAuth2AuthorizedClient obterClienteComEscopos(Authentication authentication,
                                                         Set<String> escoposAdicinais,
                                                         String state,
                                                         String redirectUri) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(clientRegistrationId, authentication.getName());

        if (client == null) {
            throw new IllegalStateException("Cliente OAuth2 não encontrado para o usuário: " + authentication.getName());
        }

        if (tokenEstaParaExpirar(client.getAccessToken())) {
            client = obterClienteAutorizado(authentication);
        }

        Set<String> escoposDoToken = client.getAccessToken().getScopes();

        if (escoposDoToken == null || !escoposDoToken.containsAll(escoposAdicinais)) {
            throw new MissingScopeException(
                    "Usuário não concedeu todos os escopos necessários",
                    escoposAdicinais,
                    escoposDoToken,
                    construirUrlIncremental(escoposAdicinais, authentication, state, redirectUri)
            );
        }

        return client;
    }

    @Override
    public String construirUrlIncremental(Set<String> escoposAdicionais, Authentication authentication, String state, String redirectUri) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(clientRegistrationId, authentication.getName());

        if (client == null) {
            throw new IllegalStateException("Não foi possível obter o cliente autorizado para: " + clientRegistrationId);
        }

        ClientRegistration registration = client.getClientRegistration();

        return UriComponentsBuilder
                .fromUriString(registration.getProviderDetails().getAuthorizationUri())
                .queryParam("client_id", client.getClientRegistration().getClientId())
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", String.join(" ", escoposAdicionais))
                .queryParam("include_granted_scopes", "true")
                .queryParam("access_type", "offline")
                .queryParam("state", state)
                .build()
                .toUriString();
    }

    @Override
    public void trocarCodePorToken(String code, Authentication authentication, String redirectUri) {
        OAuth2AuthorizedClient clientAtual = authorizedClientService
                .loadAuthorizedClient(clientRegistrationId, authentication.getName());

        if (clientAtual == null) {
            throw new IllegalStateException("Não foi possível obter o OAuth2AuthorizedClient para " + clientRegistrationId);
        }

        ClientRegistration registration = clientAtual.getClientRegistration();

        String state = "incremental-" + UUID.randomUUID();

        OAuth2AuthorizationRequest authRequest = OAuth2AuthorizationRequest.authorizationCode()
                .clientId(registration.getClientId())
                .authorizationUri(registration.getProviderDetails().getAuthorizationUri())
                .redirectUri(redirectUri)
                .state(state)
                .build();

        OAuth2AuthorizationCodeGrantRequest grantRequest =
                new OAuth2AuthorizationCodeGrantRequest(
                        registration,
                        new OAuth2AuthorizationExchange(
                            authRequest,
                            OAuth2AuthorizationResponse.success(code)
                                .redirectUri(redirectUri)
                                .state(state)
                                .build()
                ));

        RestClientAuthorizationCodeTokenResponseClient tokenResponseClient = new RestClientAuthorizationCodeTokenResponseClient();
        OAuth2AccessTokenResponse tokenResponse = tokenResponseClient.getTokenResponse(grantRequest);

        OAuth2AuthorizedClient clientAutualizado = new OAuth2AuthorizedClient(
                registration,
                authentication.getName(),
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken() == null ? clientAtual.getRefreshToken() : tokenResponse.getRefreshToken()
        );

        authorizedClientService.saveAuthorizedClient(clientAutualizado, authentication);
    }
}
