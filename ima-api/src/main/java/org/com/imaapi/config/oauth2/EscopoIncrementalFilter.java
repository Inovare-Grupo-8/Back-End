package org.com.imaapi.config.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.impl.OauthTokenServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class EscopoIncrementalFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(EscopoIncrementalFilter.class);

    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final OauthTokenServiceImpl oauthTokenService;
    private final UsuarioRepository usuarioRepository;

    public EscopoIncrementalFilter(
            OAuth2AuthorizedClientManager authorizedClientManager,
            OauthTokenServiceImpl oauthTokenService,
            UsuarioRepository usuarioRepository
    ) {
        this.authorizedClientManager = authorizedClientManager;
        this.oauthTokenService = oauthTokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        logger.debug("Processando requisição: {}?{}", requestUri, queryString);

        // Verifica se é o callback do Google
        if (requestUri.startsWith("/login/oauth2/code/google")) {
            String state = request.getParameter("state");
            logger.info("Callback OAuth2 recebido. State: {}", state);

            if (state != null && state.startsWith("calendar_")) {
                logger.info("Identificado fluxo de calendário");

                // Obtém a autenticação atual
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication == null) {
                    logger.warn("Autenticação não encontrada no contexto de segurança");
                    filterChain.doFilter(request, response);
                    return;
                }

                if (!(authentication instanceof OAuth2AuthenticationToken)) {
                    logger.warn("Tipo de autenticação inválido: {}", authentication.getClass());
                    filterChain.doFilter(request, response);
                    return;
                }

                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

                OAuth2User usuarioOauth = (OAuth2User) oauthToken.getPrincipal();
                String email = usuarioOauth.getAttribute("email");
                logger.info("Processando autorização incremental para: {}", email);

                try {
                    // Obtém os tokens atualizados
                    OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(
                            OAuth2AuthorizeRequest.withClientRegistrationId("google")
                                    .principal(authentication)
                                    .attribute(HttpServletRequest.class.getName(), request)
                                    .attribute(HttpServletResponse.class.getName(), response)
                                    .build()
                    );

                    if (authorizedClient != null) {
                        // Salva os novos tokens
                        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
                            oauthTokenService.salvarToken(
                                    usuario,
                                    authorizedClient.getAccessToken(),
                                    authorizedClient.getRefreshToken()
                            );
                            logger.info("Tokens de calendário salvos para: {}", email);
                        });
                    } else {
                        logger.error("Falha ao obter cliente autorizado");
                    }
                } catch (Exception e) {
                    logger.error("Erro ao processar autorização incremental", e);
                }
            }
        }


        filterChain.doFilter(request, response);
    }
}
