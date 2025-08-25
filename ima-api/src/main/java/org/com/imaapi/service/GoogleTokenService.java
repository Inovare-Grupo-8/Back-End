package org.com.imaapi.service;

import org.com.imaapi.model.oauth.OauthToken;
import org.com.imaapi.model.usuario.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

import java.util.Set;

public interface GoogleTokenService {
    void salvarToken(Usuario usuario, OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken);
    boolean tokenExpirou(OauthToken token);
    boolean contemEscoposNecessarios(Integer idUsuario, Set<String> escopos);
    boolean contemEscoposComClienteOAuth(Authentication authentication, Set<String> escopos);
    void renovarAccessToken(Authentication authentication);
    String construirUrlIncremental(Set<String> escoposAdicionais);
}
