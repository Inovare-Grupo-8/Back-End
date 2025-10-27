package org.com.imaapi.config.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.imaapi.config.GerenciadorTokenJwt;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.UsuarioDetalhes;
import org.com.imaapi.model.usuario.UsuarioMapper;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.impl.UsuarioServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;

import java.io.IOException;

public class AutenticacaoSucessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final GerenciadorTokenJwt gerenciadorTokenJwt;
    private final UsuarioServiceImpl usuarioService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final SecurityContextRepository securityContextRepository;

    public AutenticacaoSucessHandler(
            UsuarioRepository usuarioRepository,
            GerenciadorTokenJwt gerenciadorTokenJwt,
            UsuarioServiceImpl usuarioService,
            OAuth2AuthorizedClientService authorizedClientService,
            SecurityContextRepository securityContextRepository) {
        this.usuarioRepository = usuarioRepository;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
        this.usuarioService = usuarioService;
        this.authorizedClientService = authorizedClientService;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User usuarioOauth = oauthToken.getPrincipal();

        String email = usuarioOauth.getAttribute("email");
        Boolean emailVerificado = usuarioOauth.getAttribute("email_verified");

        if (emailVerificado != null && !emailVerificado) {
            throw new ServletException("Email não verificado pelo provedor OAuth2.");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseGet(() -> usuarioService.cadastrarUsuarioOAuth(usuarioOauth));

        UsuarioDetalhes usuarioDetalhes = UsuarioMapper.ofDetalhes(usuario, usuario.getFicha());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                usuarioDetalhes,
                null,
                usuarioDetalhes.getAuthorities()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient(
                        oauthToken.getAuthorizedClientRegistrationId(),
                        oauthToken.getName()
                );

        if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
            authorizedClientService.removeAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );

            OAuth2AuthorizedClient clientComEmail = new OAuth2AuthorizedClient(
                    authorizedClient.getClientRegistration(),
                    email,
                    authorizedClient.getAccessToken(),
                    authorizedClient.getRefreshToken() != null
                            ? authorizedClient.getRefreshToken()
                            : getRefreshTokenFromDatabase(email, oauthToken.getAuthorizedClientRegistrationId())
            );

            authorizedClientService.saveAuthorizedClient(clientComEmail, authenticationToken);
        }

        String tokenJwt = gerarJwt(authenticationToken);
        String redirectUrl = "http://localhost:5173/oauth/callback?token=" + tokenJwt;

        String originalUrl = (String) request.getSession().getAttribute("ORIGINAL_URL");

        if (originalUrl != null && originalUrl.contains("/calendar")) {
            redirectUrl += "&origin=calendar";
            request.getSession().removeAttribute("ORIGINAL_URL");
        }

        response.sendRedirect(redirectUrl);
    }

    private String gerarJwt(Authentication authentication) {
        String email = null;

        Object principal = authentication.getPrincipal();
        if (principal instanceof UsuarioDetalhes usuarioDetalhes) {
            email = usuarioDetalhes.getUsername();
        }

        usuarioRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Usuário não foi cadastrado")
        );

        return gerenciadorTokenJwt.generateToken(authentication);
    }

    private OAuth2RefreshToken getRefreshTokenFromDatabase(String email, String clientRegistrationId) {
        OAuth2AuthorizedClient clientExistente =
                authorizedClientService.loadAuthorizedClient(clientRegistrationId, email);

        if (clientExistente != null) {
            return clientExistente.getRefreshToken();
        }
        return null;
    }

}
