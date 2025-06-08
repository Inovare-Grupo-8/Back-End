package org.com.imaapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.impl.GoogleCalendarServiceImpl;
import org.com.imaapi.service.impl.OauthTokenServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;
import java.util.UUID;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@Controller
@RequestMapping("/oauth2")
public class Oauth2Controller {

    private final OauthTokenServiceImpl oauthTokenService;
    private final UsuarioRepository usuarioRepository;
    private final GoogleCalendarServiceImpl googleCalendarService;

    public Oauth2Controller(UsuarioRepository usuarioRepository,
                            OauthTokenServiceImpl oauthTokenService,
                            GoogleCalendarServiceImpl googleCalendarService) {

        this.usuarioRepository = usuarioRepository;
        this.oauthTokenService = oauthTokenService;
        this.googleCalendarService = googleCalendarService;
    }

    @GetMapping("/authorize/calendar")
    public ResponseEntity<?> authorizeCalendar(Authentication authentication) throws IOException {
        try {
            // Verificar se é autenticação OAuth2
            if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Autenticação inválida. Requer autenticação OAuth2");
            }

            OAuth2User usuarioOauth = oauthToken.getPrincipal();
            String email = usuarioOauth.getAttribute("email");

            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Verificar se o usuário já tem o escopo necessário
            try {
                if (googleCalendarService.usuarioTemEscopoCalendar(usuario.getIdUsuario())) {
                    return ResponseEntity.status(HttpStatus.OK)
                            .body("Usuário já possui permissão para Google Calendar");
                }
            } catch (GeneralSecurityException e) {
                // Tratar erros de verificação de escopo
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erro ao verificar permissões: " + e.getMessage());
            }

            String state = "calendar_" + UUID.randomUUID();

            Set<String> additionalScopes = Set.of(
                    "https://www.googleapis.com/auth/calendar.app.created"
            );

            String authorizationUrl = oauthTokenService.construirUrl(additionalScopes, state);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", authorizationUrl)
                    .build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro: " + e.getMessage());
        }
    }
}
