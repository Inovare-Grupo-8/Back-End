package org.com.imaapi.service;

import com.google.auth.oauth2.GoogleCredentials;
import org.com.imaapi.model.oauth.OauthToken;
import org.com.imaapi.model.usuario.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

public interface OauthTokenService {
    String buscarAccessTokenValido(Usuario usuario);
    GoogleCredentials buscarCredenciaisValidasGoogle(Usuario usuario);
    OAuth2AccessToken renovarAccessTokenComRefreshToken(Usuario usuario);
    OAuth2AccessToken renovarAccessToken(Authentication authentication);
    void salvarToken(Usuario usuario, OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken);
    boolean tokenExpirou(OauthToken token);
}
