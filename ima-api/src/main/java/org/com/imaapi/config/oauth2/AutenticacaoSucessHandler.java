package org.com.imaapi.config.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.imaapi.config.GerenciadorTokenJwt;
import org.com.imaapi.model.oauth.OauthToken;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.impl.OauthTokenServiceImpl;
import org.com.imaapi.service.impl.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Optional;

public class AutenticacaoSucessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GerenciadorTokenJwt gerenciadorTokenJwt;

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @Autowired
    @Qualifier("googleAuthorizedClientManager")
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @Autowired
    private OauthTokenServiceImpl oauthTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User usuarioOauth = (OAuth2User) authenticationToken.getPrincipal();
        String email = usuarioOauth.getAttribute("email");
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        if (usuarioOptional.isEmpty()) {
            usuarioService.cadastrarUsuarioOAuth(usuarioOauth);
            usuarioOptional = usuarioRepository.findByEmail(email);
        }

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(authenticationToken.getAuthorizedClientRegistrationId())
                .principal(authentication)
                .attribute(HttpServletRequest.class.getName(), request)
                .attribute(HttpServletResponse.class.getName(), response)
                .build();

        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

        if(authorizedClient != null && usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
            OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

            oauthTokenService.salvarToken(usuario, accessToken, refreshToken);
        }


        String token = gerenciadorTokenJwt.generateToken(authentication);
        response.setHeader("Authorization", "Bearer " + token);
    }

}
