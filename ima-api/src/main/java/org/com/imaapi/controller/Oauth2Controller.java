package org.com.imaapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.imaapi.service.OauthTokenService;
import org.com.imaapi.service.impl.OauthTokenServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Controller
@RequestMapping("/oauth2")
public class Oauth2Controller {
    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Qualifier("googleAuthorizedClientManager")
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;

    private final OauthTokenServiceImpl oauthTokenService;

    public Oauth2Controller(@Qualifier("googleAuthorizedClientManager") OAuth2AuthorizedClientManager authorizedClientManager,
                            OAuth2AuthorizedClientRepository authorizedClientRepository,
                            OauthTokenServiceImpl oauthTokenService) {

        this.authorizedClientManager = authorizedClientManager;
        this.authorizedClientRepository = authorizedClientRepository;
        this.oauthTokenService = oauthTokenService;
    }

    @GetMapping("/authorize/calendar")
    public void authorizeCalendar(Authentication authentication) throws IOException {
        Set<String> escopos = "https://www.googleapis.com/auth/calendar.app.created";
        oauthTokenService.autorizarComEscopos(authentication, "https://www.googleapis.com/auth/calendar.app.created");
    }

}
