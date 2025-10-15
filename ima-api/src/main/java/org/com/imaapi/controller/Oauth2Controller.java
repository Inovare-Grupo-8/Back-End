package org.com.imaapi.controller;

import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.GoogleTokenService;
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

    public Oauth2Controller(GoogleTokenServiceImpl oauthTokenService) {
        this.oauthTokenService = oauthTokenService;
    }

    @GetMapping("/authorize/calendar")
    public ResponseEntity<?> authorizeCalendar(Authentication authentication) {
        try {
            Set<String> escopoAdicional = Set.of(
                    "https://www.googleapis.com/auth/calendar.app.created",
                    "https://www.googleapis.com/auth/calendar.calendarlist"
            );
            String clientRegistrationId = "google";

            String urlAutorizacao = oauthTokenService.construirUrlIncremental(escopoAdicional, authentication, clientRegistrationId);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", urlAutorizacao)
                    .build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/googlecallback")
    public ResponseEntity<?> callback(@RequestParam String code,
                                      Authentication authentication) {
        try {
            String clientRegistrationId = "google";
            OAuth2AuthorizedClient client = oauthTokenService.trocarCodePorToken(code, authentication, clientRegistrationId);
            return ResponseEntity.ok("Token salvo com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
