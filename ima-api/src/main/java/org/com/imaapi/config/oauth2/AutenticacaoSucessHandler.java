package org.com.imaapi.config.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.imaapi.config.GerenciadorTokenJwt;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.impl.OauthTokenServiceImpl;
import org.com.imaapi.service.impl.UsuarioServiceImpl;
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


    private final UsuarioRepository usuarioRepository;
    private final GerenciadorTokenJwt gerenciadorTokenJwt;
    private final UsuarioServiceImpl usuarioService;
    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final OauthTokenServiceImpl oauthTokenService;

    public AutenticacaoSucessHandler(UsuarioRepository usuarioRepository,
                                     GerenciadorTokenJwt gerenciadorTokenJwt,
                                     UsuarioServiceImpl usuarioService,
                                     @Qualifier("googleAuthorizedClientManager") OAuth2AuthorizedClientManager authorizedClientManager,
                                     OauthTokenServiceImpl oauthTokenService) {
        this.usuarioRepository = usuarioRepository;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
        this.usuarioService = usuarioService;
        this.authorizedClientManager = authorizedClientManager;
        this.oauthTokenService = oauthTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        System.out.println("=== INICIANDO onAuthenticationSuccess ===");

        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User usuarioOauth = (OAuth2User) authenticationToken.getPrincipal();
        String email = usuarioOauth.getAttribute("email");
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        if (email == null) {
            throw new ServletException("Email não encontrado para o OAuth2");
        }

        if (usuarioOptional.isEmpty()) {
            usuarioService.cadastrarUsuarioOAuth(usuarioOauth);
            usuarioOptional = usuarioRepository.findByEmail(email);

            if (usuarioOptional.isEmpty()) {
                throw new ServletException("Erro ao cadastrar usuário");
            }
        }

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(authenticationToken.getAuthorizedClientRegistrationId())
                .principal(authentication)
                .attribute(HttpServletRequest.class.getName(), request)
                .attribute(HttpServletResponse.class.getName(), response)
                .build();

        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

        if(authorizedClient != null) {
            Usuario usuario = usuarioOptional.get();
            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
            OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

            System.out.println("Access Token: " + accessToken.getTokenValue());
            System.out.println("Refresh Token: " +
                    (refreshToken != null ? refreshToken.getTokenValue() : "null"));

            if(refreshToken == null) {
                System.out.println("Não foi recebido um refresh token");
            }

            oauthTokenService.salvarToken(usuario, accessToken, refreshToken);
        } else {
            throw new ServletException("Erro ao obter tokens autorizados com o google");
        }

        UsuarioTokenOutput tokenOutput = usuarioService.autenticar(usuarioOptional.get());
        response.setHeader("Authorization", "Bearer " + tokenOutput.getToken());

        System.out.println("=== FINALIZANDO onAuthenticationSuccess ===");
    }

}
