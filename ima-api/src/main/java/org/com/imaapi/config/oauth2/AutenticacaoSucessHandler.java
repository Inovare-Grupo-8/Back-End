package org.com.imaapi.config.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.imaapi.config.GerenciadorTokenJwt;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.UsuarioMapper;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.impl.GoogleTokenServiceImpl;
import org.com.imaapi.service.impl.UsuarioServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class AutenticacaoSucessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final GerenciadorTokenJwt gerenciadorTokenJwt;
    private final UsuarioServiceImpl usuarioService;
    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final GoogleTokenServiceImpl oauthTokenService;

    public AutenticacaoSucessHandler(
            UsuarioRepository usuarioRepository,
            GerenciadorTokenJwt gerenciadorTokenJwt,
            UsuarioServiceImpl usuarioService,
            OAuth2AuthorizedClientManager authorizedClientManager,
            GoogleTokenServiceImpl oauthTokenService) {
        this.usuarioRepository = usuarioRepository;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
        this.usuarioService = usuarioService;
        this.authorizedClientManager = authorizedClientManager;
        this.oauthTokenService = oauthTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User usuarioOauth = oauthToken.getPrincipal();

        String email = usuarioOauth.getAttribute("email");
        Boolean emailVerificado = usuarioOauth.getAttribute("email_verified");

        if (email == null) {
            throw new ServletException("Email não encontrado no provedor OAuth2.");
        }

        if (emailVerificado != null && !emailVerificado) {
            throw new ServletException("Email não verificado pelo provedor OAuth2.");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseGet(() -> usuarioService.cadastrarUsuarioOAuth(usuarioOauth));

        OAuth2AuthorizedClient authorizedClient = authorizeClient(oauthToken, request, response);

        if (authorizedClient == null) {
            throw new ServletException("Erro ao obter tokens autorizados com o Google.");
        }

        oauthTokenService.processarTokenOAuth2(usuario, authorizedClient, authentication);

        SecurityContextHolder.getContext().setAuthentication(
                new AppUserAuthenticationToken(usuario,
                        usuario.getSenha(),
                        UsuarioMapper.ofDetalhes(usuario, usuario.getFicha()).getAuthorities(),
                        "google",
                        authorizedClient)
        );

        gerarJwt(authentication, response);
    }

    private OAuth2AuthorizedClient authorizeClient(OAuth2AuthenticationToken oauthToken,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(oauthToken.getAuthorizedClientRegistrationId())
                .principal(oauthToken)
                .attribute(HttpServletRequest.class.getName(), request)
                .attribute(HttpServletResponse.class.getName(), response)
                .build();

        return authorizedClientManager.authorize(authorizeRequest);
    }

    private void gerarJwt(Authentication authentication, HttpServletResponse response) throws IOException {
        String tokenJwt = gerenciadorTokenJwt.generateToken(authentication);
        Usuario usuario = (Usuario) authentication.getPrincipal();
        UsuarioTokenOutput tokenOutput = UsuarioMapper.of(usuario, tokenJwt);

        response.setHeader("Authorization", "Bearer " + tokenOutput.getToken());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"token\": \"" + tokenOutput.getToken() + "\"}");
    }

}
