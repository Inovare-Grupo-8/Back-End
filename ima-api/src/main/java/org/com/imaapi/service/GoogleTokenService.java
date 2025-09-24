package org.com.imaapi.service;

import jakarta.servlet.ServletException;
import org.com.imaapi.model.usuario.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

import java.util.Set;

public interface GoogleTokenService {
    void processarTokenOAuth2(Usuario usuario, OAuth2AuthorizedClient authorizedClient, Authentication authentication) throws ServletException;
    void salvarAccessToken(Usuario usuario, OAuth2AccessToken accessToken);
    boolean possuiRefreshToken(Integer idUsuario);
    void salvarRefreshToken(Usuario usuario, OAuth2RefreshToken refreshToken);
    boolean tokenEstaParaExpirar(OAuth2AccessToken token);
    void removerTokens(Usuario usuario);
    boolean contemEscoposNecessarios(Integer idUsuario, Set<String> escopos, Authentication authentication);
    boolean contemEscoposComClienteOAuth(Authentication authentication, Set<String> escopos);
    void renovarAccessToken(Authentication authentication);
    String construirUrlIncremental(Set<String> escoposAdicionais, Authentication authentication) throws  ServletException;

    OAuth2AuthorizedClient trocarCodePorToken(String code);
}
