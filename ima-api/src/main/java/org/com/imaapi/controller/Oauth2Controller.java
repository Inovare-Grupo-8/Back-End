package org.com.imaapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

@Controller
@RequestMapping("/oauth2")
public class Oauth2Controller {
    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;

    public Oauth2Controller(OAuth2AuthorizedClientManager authorizedClientManager,
                            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        this.authorizedClientManager = authorizedClientManager;
        this.authorizedClientRepository = authorizedClientRepository;
    }

    @GetMapping("/authorize/calendar")
    public void authorizeCalendar(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Authentication authentication) throws IOException {

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("google")
                .principal(authentication)
                .attribute(OAuth2AuthorizationContext.REQUEST_SCOPE_ATTRIBUTE_NAME,
                        Collections.singleton("https://www.googleapis.com/auth/calendar.app.created"))
                .build();

        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

        if (authorizedClient != null) {
            String accessToken = authorizedClient.getAccessToken().getTokenValue();
        }

    }

}
