package org.com.imaapi.service;

import org.com.imaapi.model.usuario.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

public interface OauthTokenService {
    OAuth2AccessToken renovarAccessToken(Authentication authentication);

    void salvarToken(Usuario usuario, OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken);
}
