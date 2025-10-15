package org.com.imaapi.service.impl;

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
    private final OAuth2AuthorizedClientManager oauthClientManager;

    public GoogleTokenServiceImpl(OAuth2AuthorizedClientService authorizedClientService,
                                  OAuth2AuthorizedClientManager oauthClientManager) {
        this.oauthClientManager = oauthClientManager;
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public boolean tokenEstaParaExpirar(OAuth2AccessToken token) {
        return token.getExpiresAt() == null ||
                token.getExpiresAt().isBefore(Instant.now().minusSeconds(30));
    }

    @Override
    public boolean contemEscoposNecessarios(Set<String> escopos, String clientRegistrationId, Authentication authentication) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(clientRegistrationId, authentication.getName());
        if (client == null || client.getAccessToken() == null) {
            return false;
        }

        OAuth2AccessToken accessToken = client.getAccessToken();

        if (tokenEstaParaExpirar(accessToken)) {
            client = renovarAccessToken(authentication, clientRegistrationId);
        }

        return accessToken.getScopes().containsAll(escopos);
    }

    @Override
    public OAuth2AuthorizedClient renovarAccessToken(Authentication authentication, String clientRegistrationId) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(clientRegistrationId)
                .principal(authentication)
                .build();

        OAuth2AuthorizedClient clientAtualizado = oauthClientManager.authorize(authorizeRequest);
        if (clientAtualizado == null) {
            throw new OAuth2AuthenticationException("Falha ao renovar o token de acesso");
        }
        return clientAtualizado;
    }

    @Override
    public String construirUrlIncremental(Set<String> escoposAdicionais, Authentication authentication, String clientRegistrationId) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(clientRegistrationId, authentication.getName());

        if (client == null) {
            throw new IllegalStateException("Não foi possível obter o OAuth2AuthorizedClient para " + clientRegistrationId);
        }

        return UriComponentsBuilder
                .fromUriString(client.getClientRegistration().getProviderDetails().getAuthorizationUri())
                .queryParam("client_id", client.getClientRegistration().getClientId())
                .queryParam("redirect_uri", "http://localhost:8080/oauth2/googlecallback")
                .queryParam("response_type", "code")
                .queryParam("scope", String.join(" ", escoposAdicionais))
                .queryParam("include_granted_scopes", "true")
                .build()
                .toUriString();
    }

    @Override
    public OAuth2AuthorizedClient trocarCodePorToken(String code, Authentication authentication, String clientRegistrationId) {
        OAuth2AuthorizedClient clientAtual = authorizedClientService
                .loadAuthorizedClient(clientRegistrationId, authentication.getName());

        if (clientAtual == null) {
            throw new IllegalStateException("Não foi possível obter o OAuth2AuthorizedClient para " + clientRegistrationId);
        }

        ClientRegistration registration = clientAtual.getClientRegistration();

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

        OAuth2AuthorizedClient clientAutualizado = new OAuth2AuthorizedClient(
                registration,
                authentication.getName(),
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken() == null ? clientAtual.getRefreshToken() : tokenResponse.getRefreshToken()
        );

        authorizedClientService.saveAuthorizedClient(clientAutualizado, authentication);

        return clientAutualizado;
    }
}
