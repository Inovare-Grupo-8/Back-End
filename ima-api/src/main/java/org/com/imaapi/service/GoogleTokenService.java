package org.com.imaapi.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.Set;

public interface GoogleTokenService {
    boolean tokenEstaParaExpirar(OAuth2AccessToken token);
    boolean contemEscoposNecessarios(Set<String> escopos, Authentication authentication);
    String construirUrlIncremental(Set<String> escoposAdicionais, Authentication authentication, String state,  String redirectUri);
    String obterAccessToken(Authentication authentication);
    OAuth2AuthorizedClient obterClienteComEscopos(Authentication authentication, Set<String> escoposAdicinais, String state, String redirectUri);
    OAuth2AuthorizedClient obterClienteAutorizado(Authentication authentication);
    void trocarCodePorToken(String code, Authentication authentication, String redirectUri);
}
