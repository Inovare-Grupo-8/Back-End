package org.com.imaapi.service.impl;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.imaapi.model.oauth.OauthToken;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.repository.OauthTokenRepository;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.OauthTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class OauthTokenServiceImpl implements OauthTokenService {

    @Value("spring.security.oauth2.client.registration.google.client-id")
    private String googleClientId;

    @Value("spring.security.oauth2.client.registration.google.client-secret")
    private String googleClientSecret;

    private final OauthTokenRepository oauthTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientManager oauthClientManager;

    public OauthTokenServiceImpl(OauthTokenRepository oauthTokenRepository,
                                 OAuth2AuthorizedClientManager oauthClientManager,
                                 UsuarioRepository usuarioRepository,
                                 ClientRegistrationRepository clientRegistrationRepository) {
        this.oauthTokenRepository = oauthTokenRepository;
        this.oauthClientManager = oauthClientManager;
        this.usuarioRepository = usuarioRepository;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public String buscarAccessTokenValido(Usuario usuario) {
        OauthToken token = oauthTokenRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Token não encontrado"));

        if (tokenExpirou(token)) {
            OAuth2AccessToken newToken = renovarAccessTokenComRefreshToken(usuario);
            return newToken.getTokenValue();
        }
        return token.getAccessToken();
    }

    @Override
    public OAuth2AccessToken renovarAccessTokenComRefreshToken(Usuario usuario) {
        OauthToken oauthToken = oauthTokenRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Token não encontrado para o usuário: " + usuario.getIdUsuario()));

        try {
            GoogleCredentials credentials = UserCredentials.newBuilder()
                    .setClientId(googleClientId)
                    .setClientSecret(googleClientSecret)
                    .setRefreshToken(oauthToken.getRefreshToken())
                    .build();

            credentials.refresh();
            AccessToken novoAccessToken = credentials.getAccessToken();

            oauthToken.setAccessToken(novoAccessToken.getTokenValue());
            oauthToken.setExpiresAt(novoAccessToken.getExpirationTime().toInstant());
            oauthTokenRepository.save(oauthToken);

            return new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    novoAccessToken.getTokenValue(),
                    Instant.now(),
                    novoAccessToken.getExpirationTime().toInstant()
            );

        } catch (IOException e) {
            throw new RuntimeException("Falha ao renovar token: " + e.getMessage(), e);
        }
    }

    @Override
    public OAuth2AccessToken renovarAccessToken(Authentication authentication) {
        OAuth2User usuarioOauth = (OAuth2User) authentication.getPrincipal();
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(usuarioOauth.getAttribute("email"));

        if(usuarioOptional.isEmpty()) {
            throw new IllegalStateException("Usuário não cadastrado");
        }

        return renovarAccessTokenComRefreshToken(usuarioOptional.get());
    }

    @Override
    public void salvarToken(Usuario usuario, OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken) {
        OauthToken oauthToken = oauthTokenRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .orElse(new OauthToken());

        oauthToken.setUsuario(usuario);
        oauthToken.setAccessToken(accessToken.getTokenValue());

        if (refreshToken != null) {
            oauthToken.setRefreshToken(refreshToken.getTokenValue());
        }

        oauthToken.setExpiresAt(accessToken.getExpiresAt());

        oauthTokenRepository.save(oauthToken);
    }

    public GoogleCredentials buscarCredenciaisValidasGoogle(Usuario usuario) {
        OauthToken token = oauthTokenRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Token não encontrado"));

        if (tokenExpirou(token)) {
            renovarAccessTokenComRefreshToken(usuario);

            token = oauthTokenRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Token não encontrado após renovação"));
        }

        return UserCredentials.newBuilder()
                .setClientId(googleClientId)
                .setClientSecret(googleClientSecret)
                .setRefreshToken(token.getRefreshToken())
                .setAccessToken(new AccessToken(token.getAccessToken(), Date.from(token.getExpiresAt())))
                .build();
    }

    public boolean tokenExpirou(OauthToken token) {
        return token.getExpiresAt() == null ||
        token.getExpiresAt().isBefore(Instant.now().minusSeconds(30));
    }

    public String construirUrl(Set<String> escoposAdicionais, String state) {

        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");

        Set<String> escoposCombinados = new HashSet<>(googleRegistration.getScopes());
        escoposCombinados.addAll(escoposAdicionais);

        return UriComponentsBuilder
                .fromUriString(googleRegistration.getProviderDetails().getAuthorizationUri())
                .queryParam("client_id", googleRegistration.getClientId())
                .queryParam("redirect_uri", googleRegistration.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", String.join(" ", escoposCombinados))
                .queryParam("state", state)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .build()
                .toUriString();
    }
}
