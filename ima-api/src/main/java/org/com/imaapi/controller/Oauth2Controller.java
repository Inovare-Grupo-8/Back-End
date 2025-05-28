package org.com.imaapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.com.imaapi.service.impl.OauthTokenServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
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

    private final OauthTokenServiceImpl oauthTokenService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public Oauth2Controller(ClientRegistrationRepository clientRegistrationRepository,
                            OauthTokenServiceImpl oauthTokenService) {

        this.clientRegistrationRepository = clientRegistrationRepository;
        this.oauthTokenService = oauthTokenService;
    }

    @GetMapping("/authorize/calendar")
    public void authorizeCalendar(HttpServletRequest request,
                                  HttpServletResponse response,
                                  HttpSession session) throws IOException {
        Set<String> escopos = Set.of(
                "openid",
                "email",
                "profile",
                "https://www.googleapis.com/auth/calendar.events" // Escopo correto do Calendar
        );

        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth_state", state);

        String authorizationUrl = oauthTokenService.buildAuthorizationUrl(escopos, state);

        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/login/oauth2/code/google")
    public String HandleCallback(
            @RequestParam(required = false) String error,
            @RequestParam String state,
            HttpSession session
    ) {
        if (error != null) {
            return "redirect:/erro-de-autorizacao";
        }

        if (!state.equals(session.getAttribute("oauth_state"))) {
            throw new SecurityException("Estado inválido");
        }

        return "redirect:/"
    }

}
