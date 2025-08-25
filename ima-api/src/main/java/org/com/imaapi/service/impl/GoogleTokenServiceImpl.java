package org.com.imaapi.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");

        Set<String> escoposCombinados = new HashSet<>(googleRegistration.getScopes());
        escoposCombinados.addAll(escoposAdicionais);

        return UriComponentsBuilder
                .fromUriString(googleRegistration.getProviderDetails().getAuthorizationUri())
                .queryParam("client_id", googleRegistration.getClientId())
                .queryParam("redirect_uri", googleRegistration.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", String.join(" ", escoposCombinados))
                .queryParam("response_type", "code")
                .queryParam("include_granted_scopes", "true")
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .build()
                .toUriString();
    }
}
