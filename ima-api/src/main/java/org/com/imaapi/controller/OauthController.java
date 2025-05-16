package org.com.imaapi.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RestController
public class OauthController {
    @GetMapping("/login-google")
    public void loginWithGoogle(HttpServletResponse response) throws IOException {
        String url = UriComponentsBuilder.fromHttpUrl("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", "989483491531-52rbg5okogsm76sqhdb1vkbs321rfud0.apps.googleusercontent.com")
                .queryParam("redirect_uri", "http://localhost:8080/login/oauth2/code/google")
                .queryParam("response_type", "code")
                .queryParam("scope", "openid profile email")
                .queryParam("access_type", "offline")
                .queryParam("include_granted_scopes", "true")
                .build()
                .toUriString();;

        response.sendRedirect(url);
    }
}
