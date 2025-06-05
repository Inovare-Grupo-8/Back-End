package org.com.imaapi.config.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.imaapi.config.GerenciadorTokenJwt;
import org.com.imaapi.model.usuario.Ficha;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.UsuarioMapper;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.repository.FichaRepository;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.impl.OauthTokenServiceImpl;
import org.com.imaapi.service.impl.UsuarioServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Optional;

public class AutenticacaoSucessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final FichaRepository fichaRepository;
    private final GerenciadorTokenJwt gerenciadorTokenJwt;
    private final UsuarioServiceImpl usuarioService;
    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final OauthTokenServiceImpl oauthTokenService;

    public AutenticacaoSucessHandler(
            UsuarioRepository usuarioRepository,
            FichaRepository fichaRepository,
            GerenciadorTokenJwt gerenciadorTokenJwt,
            UsuarioServiceImpl usuarioService,
            OAuth2AuthorizedClientManager authorizedClientManager,
            OauthTokenServiceImpl oauthTokenService) {
        this.usuarioRepository = usuarioRepository;
        this.fichaRepository = fichaRepository;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
        this.usuarioService = usuarioService;
        this.authorizedClientManager = authorizedClientManager;
        this.oauthTokenService = oauthTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User usuarioOauth = authenticationToken.getPrincipal();

        String email = usuarioOauth.getAttribute("email");
        Boolean emailVerificado = usuarioOauth.getAttribute("email_verified");

        if (email == null) {
            throw new ServletException("Email não encontrado no provedor OAuth2.");
        }

        if (emailVerificado != null && !emailVerificado) {
            throw new ServletException("Email não verificado pelo provedor OAuth2.");
        }

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        if (usuarioOptional.isEmpty()) {
            usuarioService.cadastrarUsuarioOAuth(usuarioOauth);
            usuarioOptional = usuarioRepository.findByEmail(email);

            if (usuarioOptional.isEmpty()) {
                throw new ServletException("Erro ao cadastrar usuário OAuth2.");
            }
        }

        Usuario usuario = usuarioOptional.get();

        // Obter tokens OAuth2
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(authenticationToken.getAuthorizedClientRegistrationId())
                .principal(authentication)
                .attribute(HttpServletRequest.class.getName(), request)
                .attribute(HttpServletResponse.class.getName(), response)
                .build();

        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

        if (authorizedClient != null) {
            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
            OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

            oauthTokenService.salvarToken(usuario, accessToken, refreshToken);
        } else {
            throw new ServletException("Erro ao obter tokens autorizados com o Google.");
        }

        Ficha ficha = fichaRepository.findById(usuario.getFicha().getIdFicha())
                .orElseThrow(() -> new ServletException("Ficha não encontrada para o usuário."));

        Authentication auth = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(), null, UsuarioMapper.ofDetalhes(usuario, ficha).getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        // Gera token JWT
        String token = gerenciadorTokenJwt.generateToken(auth);
        UsuarioTokenOutput tokenOutput = UsuarioMapper.of(usuario, token);

        response.setHeader("Authorization", "Bearer " + tokenOutput.getToken());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"token\": \"" + tokenOutput.getToken() + "\"}");
    }

}
