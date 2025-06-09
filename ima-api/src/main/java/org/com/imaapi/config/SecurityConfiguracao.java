package org.com.imaapi.config;

import org.com.imaapi.config.oauth2.AutenticacaoSucessHandler;
import org.com.imaapi.config.oauth2.EscopoIncrementalFilter;
import org.com.imaapi.repository.FichaRepository;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.impl.AutenticacaoServiceImpl;
import org.com.imaapi.service.impl.OauthTokenServiceImpl;
import org.com.imaapi.service.impl.UsuarioServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguracao {    private static final AntPathRequestMatcher[] URLS_PERMITIDAS = {
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/swagger-ui.html"),
            new AntPathRequestMatcher("/swagger-resources"),
            new AntPathRequestMatcher("/swagger-resources/**"),
            new AntPathRequestMatcher("/configuration/ui"),
            new AntPathRequestMatcher("/configuration/security"),
            new AntPathRequestMatcher("/api/public/**"),
            new AntPathRequestMatcher("/api/public/authenticate"),
            new AntPathRequestMatcher("/webjars/**"),
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/actuator/**"),
            new AntPathRequestMatcher("/usuarios/**"),
            new AntPathRequestMatcher("/assistentes-sociais/**"),            
            new AntPathRequestMatcher("/usuarios/login/***"),
            new AntPathRequestMatcher("/h2-console/**"),
            new AntPathRequestMatcher("/h2-console/**/**"),
            new AntPathRequestMatcher("/perfil/**"),
            new AntPathRequestMatcher("/error/**"),
            new AntPathRequestMatcher("/"),
            new AntPathRequestMatcher("/oauth2/**"),
            new AntPathRequestMatcher("/oauth2/authorization/google"),
            new AntPathRequestMatcher("/dev/token"),
            new AntPathRequestMatcher("/uploads/**")  // Add this line
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AutenticacaoSucessHandler autenticacaoSucessHandler,
                                                   AutenticacaoEntryPoint autenticacaoEntryPoint,
                                                   EscopoIncrementalFilter escopoIncrementalFilter) throws Exception {
        return http
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .cors(Customizer.withDefaults())
                .csrf(CsrfConfigurer<HttpSecurity>::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/usuarios/voluntario").hasRole("ADMINISTRADOR")
                        .requestMatchers(URLS_PERMITIDAS)
                        .permitAll()
                        .anyRequest().authenticated()  // Requer autenticação para tudo
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(autenticacaoSucessHandler)
                )
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(autenticacaoEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .addFilterBefore(jwtAuthenticationFilterBean(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(escopoIncrementalFilter, OAuth2AuthorizationRequestRedirectFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(new AutenticacaoProvider(autenticacaoService(), passwordEncoder()));
        return authenticationManagerBuilder.build();
    }

    @Bean
    public AutenticacaoEntryPoint jwtAuthenticationEntryPointBean() {
        return new AutenticacaoEntryPoint();
    }

    @Bean
    public GerenciadorTokenJwt jwtAuthenticationUtilBean() {
        return new GerenciadorTokenJwt();
    }

    @Bean
    public AutenticacaoFilter jwtAuthenticationFilterBean() {
        return new AutenticacaoFilter(autenticacaoService(), jwtAuthenticationUtilBean());
    }

    @Bean
    public AutenticacaoSucessHandler autenticacaoSucessHandler(
            UsuarioRepository usuarioRepository,
            FichaRepository fichaRepository,
            GerenciadorTokenJwt gerenciadorTokenJwt,
            UsuarioServiceImpl usuarioService,
            OAuth2AuthorizedClientManager authorizedClientManager,
            OauthTokenServiceImpl oauthTokenService) {

        return new AutenticacaoSucessHandler(
                usuarioRepository,
                fichaRepository,
                gerenciadorTokenJwt,
                usuarioService,
                authorizedClientManager,
                oauthTokenService
        );
    }

    @Bean
    public EscopoIncrementalFilter escopoIncrementalFilter(
            OAuth2AuthorizedClientManager authorizedClientManager,
            OauthTokenServiceImpl oauthTokenService,
            UsuarioRepository usuarioRepository) {
        return new EscopoIncrementalFilter(
                authorizedClientManager,
                oauthTokenService,
                usuarioRepository
        );
    }

    @Bean
    public AuthenticationEntryPoint autenticacaoEntryPoint() {
        return new AutenticacaoEntryPoint();
    }

    @Bean
    public AutenticacaoServiceImpl autenticacaoService() {
        return new AutenticacaoServiceImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Wrap the standard password encoder with our logging version
        return new LoggingPasswordEncoder(new BCryptPasswordEncoder());
    }
                                   
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracao = new CorsConfiguration();
        configuracao.setAllowedOrigins(Arrays.asList("http://localhost:3030", "http://localhost:5173"));
        configuracao.setAllowedMethods(
                Arrays.asList(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.OPTIONS.name(),
                        HttpMethod.HEAD.name(),
                        HttpMethod.TRACE.name()
                )
        );

        // Allow all headers to fix CORS issues
        configuracao.setAllowedHeaders(List.of("*"));
        configuracao.setAllowCredentials(true);

        configuracao.setExposedHeaders(List.of(
                HttpHeaders.CONTENT_DISPOSITION,
                "Authorization"
        ));

        UrlBasedCorsConfigurationSource origem = new UrlBasedCorsConfigurationSource();
        origem.registerCorsConfiguration("/**", configuracao);

        return origem;
    }
}
