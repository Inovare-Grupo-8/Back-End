package org.com.imaapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.imaapi.service.impl.GoogleTokenServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/oauth2")
public class Oauth2Controller {

    private final GoogleTokenServiceImpl googleTokenService;

    public Oauth2Controller(GoogleTokenServiceImpl googleTokenService) {
        this.googleTokenService = googleTokenService;
    }

    @GetMapping("/authorize/calendar")
    public void authorizeCalendar(Authentication authentication,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws IOException {
        Set<String> escopoAdicional = Set.of(
                "https://www.googleapis.com/auth/calendar.app.created",
                "https://www.googleapis.com/auth/calendar.calendarlist"
        );

        String state = "incremental-" + UUID.randomUUID();
        request.getSession().setAttribute("OAUTH2_STATE", state);

        String redirectUri = request.getRequestURL().toString().replace(request.getRequestURI(), "/oauth2/googlecallback");
        String urlAutorizacao = googleTokenService.construirUrlIncremental(escopoAdicional, authentication, state, redirectUri);

        response.sendRedirect(urlAutorizacao);
    }

    @GetMapping("/googlecallback")
    public ResponseEntity<?> callback(@RequestParam String code,
                                      @RequestParam String state,
                                      HttpServletRequest request,
                                      Authentication authentication) {
        String sessionState = (String) request.getSession().getAttribute("OAUTH2_STATE");
        if (sessionState == null || !sessionState.equals(state)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("State inválido ou expirado");
        }

        request.getSession().removeAttribute("OAUTH2_STATE");

        String redirectUri = request.getRequestURL().toString();
        googleTokenService.trocarCodePorToken(code, authentication, redirectUri);

        String redirectUrl = (String) request.getSession().getAttribute("ORIGINAL_URL");
        if (redirectUrl == null) { redirectUrl = "/"; }
        request.getSession().removeAttribute("ORIGINAL_URL");

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", redirectUrl)
                .build();
    }
}
