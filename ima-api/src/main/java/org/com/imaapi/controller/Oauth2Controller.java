package org.com.imaapi.controller;

import org.com.imaapi.config.oauth2.AppUserAuthenticationToken;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.impl.GoogleTokenServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
@RequestMapping("/oauth2")
public class Oauth2Controller {

    private final GoogleTokenServiceImpl oauthTokenService;
    private final UsuarioRepository usuarioRepository;

    public Oauth2Controller(UsuarioRepository usuarioRepository,
                            GoogleTokenServiceImpl oauthTokenService) {

        this.usuarioRepository = usuarioRepository;
        this.oauthTokenService = oauthTokenService;
    }

    @GetMapping("/authorize/calendar")
    public ResponseEntity<?> authorizeCalendar(Authentication authentication) {
        try {
            OAuth2AuthorizedClient client = null;
            if (authentication instanceof AppUserAuthenticationToken appToken) {
                client = appToken.getAuthorizedClient();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuário não autenticado via Google");
            }

            if (client == null) {
                throw new IllegalStateException("Não foi possível obter o OAuth2AuthorizedClient");
            }

            Set<String> escopoAdicional = Set.of("https://www.googleapis.com/auth/calendar.app.created");
            String urlAutorizacao = oauthTokenService.construirUrlIncremental(escopoAdicional, authentication);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", urlAutorizacao)
                    .build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/googlecallback")
    public ResponseEntity<?> callback(@RequestParam String code) {
        try {
            OAuth2AuthorizedClient client = oauthTokenService.trocarCodePorToken(code);
            return ResponseEntity.ok("Token salvo com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
