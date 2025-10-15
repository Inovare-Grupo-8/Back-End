package org.com.imaapi.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.Set;

public interface GoogleTokenService {
    boolean tokenEstaParaExpirar(OAuth2AccessToken token);
    boolean contemEscoposNecessarios(Set<String> escopos, String clientRegistrationId, Authentication authentication);
    String construirUrlIncremental(Set<String> escoposAdicionais, Authentication authentication, String clientRegistrationId);
    OAuth2AuthorizedClient renovarAccessToken(Authentication authentication, String clientRegistrationId);

    OAuth2AuthorizedClient trocarCodePorToken(String code, Authentication authentication, String clientRegistrationId);
}
