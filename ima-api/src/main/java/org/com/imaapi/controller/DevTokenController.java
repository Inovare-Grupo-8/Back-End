package org.com.imaapi.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import org.com.imaapi.model.usuario.input.DevTokenInput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dev")
@Profile("dev")
@CrossOrigin(origins = "*")
public class DevTokenController {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.validity}")
    private long defaultJwtTokenValidity;

    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> generateDevToken(@RequestBody @Valid DevTokenInput input) {
        long tokenValidity = input.getValidityInSeconds() != null
                ? input.getValidityInSeconds()
                : defaultJwtTokenValidity;

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        String authorities = String.join(",", input.getAuthorities());

        String token = Jwts.builder()
                .subject(input.getEmail())
                .claim("authorities", authorities)
                .claim("nome", input.getNome())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenValidity * 1000))
                .signWith(key, Jwts.SIG.HS256)
                .compact();

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        response.put("expiresIn", String.valueOf(tokenValidity));

        return ResponseEntity.ok(response);
    }
}
