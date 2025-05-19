package org.com.imaapi.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.imaapi.service.impl.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class AutenticacaoSucessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private GerenciadorTokenJwt gerenciadorTokenJwt;

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User usuario = (OAuth2User) authentication.getPrincipal();
        Boolean emailVerificado = usuario.getAttribute("email_verificado");


        if (Boolean.FALSE.equals(emailVerificado)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Email não verificado");
            return;
        }

        usuarioService.cadastrarVoluntarioOAuth(usuario);
        String token = gerenciadorTokenJwt.generateToken(authentication);
        response.setHeader("Authorization", "Bearer " + token);
    }

}
