package org.com.imaapi.config.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.imaapi.config.GerenciadorTokenJwt;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.UsuarioDetalhes;
import org.com.imaapi.model.usuario.UsuarioMapper;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.impl.UsuarioServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class AutenticacaoSucessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final GerenciadorTokenJwt gerenciadorTokenJwt;
    private final UsuarioServiceImpl usuarioService;

    public AutenticacaoSucessHandler(
            UsuarioRepository usuarioRepository,
            GerenciadorTokenJwt gerenciadorTokenJwt,
            UsuarioServiceImpl usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
        this.usuarioService = usuarioService;
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

        SecurityContextHolder.getContext().setAuthentication(
                new AppUserAuthenticationToken(
                        usuarioDetalhes,
                        usuarioDetalhes.getAuthorities(),
                        "google",
                        null)
        );

        gerarJwt(SecurityContextHolder.getContext().getAuthentication(), response);
    }

    private void gerarJwt(Authentication authentication, HttpServletResponse response) throws IOException {
        UsuarioDetalhes usuarioDetalhes = (UsuarioDetalhes) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(usuarioDetalhes.getUsername()).orElseThrow(
                () -> new RuntimeException("Usuário não foi cadastrado")
        );

        String tokenJwt = gerenciadorTokenJwt.generateToken(authentication);
        UsuarioTokenOutput tokenOutput = UsuarioMapper.of(usuario, tokenJwt);

        response.setHeader("Authorization", "Bearer " + tokenOutput.getToken());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"token\": \"" + tokenOutput.getToken() + "\"}");
    }

}
