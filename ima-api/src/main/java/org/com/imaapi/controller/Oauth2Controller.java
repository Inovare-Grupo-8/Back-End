package org.com.imaapi.controller;

import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.impl.OauthTokenServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/oauth2")
public class Oauth2Controller {

    private final OauthTokenServiceImpl oauthTokenService;
    private final UsuarioRepository usuarioRepository;

    public Oauth2Controller(UsuarioRepository usuarioRepository,
                            OauthTokenServiceImpl oauthTokenService) {

        this.usuarioRepository = usuarioRepository;
        this.oauthTokenService = oauthTokenService;
    }

    @GetMapping("/authorize/calendar")
    public ResponseEntity<?> authorizeCalendar(Authentication authentication) throws IOException {
        try {
            OAuth2User usuarioOauth = (OAuth2User) authentication.getPrincipal();
            System.out.println(usuarioOauth);
            String email = usuarioOauth.getAttribute("email");

            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            String state = "calendar_" + UUID.randomUUID().toString();

            Set<String> additionalScopes = Set.of(
                    "https://www.googleapis.com/auth/calendar.app.created"
            );

            String authorizationUrl = oauthTokenService.construirUrl(additionalScopes, state);

            System.out.println(authorizationUrl);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", authorizationUrl)
                    .build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
