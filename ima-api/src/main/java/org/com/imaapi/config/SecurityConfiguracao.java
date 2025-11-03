package org.com.imaapi.config;

import org.com.imaapi.config.oauth2.AutenticacaoSucessHandler;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.impl.AutenticacaoServiceImpl;
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
import org.springframework.security.oauth2.client.*;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguracao {
    private static final String[] URLS_PUBLICAS = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/api/public/**",
            "/api/public/authenticate",
            "/webjars/**",
            "/v3/api-docs/**",
            "/actuator/**",
            "/usuarios/fase1/**",
            "/usuarios/fase2/**",
            "/usuarios/login/**",
            "/h2-console/**",
            "/h2-console/**/**",
            "/perfil/**",
            "/error/**",
            "/",
            "/oauth2/**",
            "/dev/token",
            "/uploads/**",
            "/usuarios/fase1",
            "/usuarios/fase2"
    };

    private static final String[] URLS_ADMINISTRADORES = {
            "/{id}/classificar-usuarios",
            "/usuarios/classificar-usuarios/**",
            "/usuarios/{id}/classificar/**",
            "/{id}", "DELETE",
            "/usuarios/nao-classificados/**",
            "/assistentes-sociais/**",
            "/usuarios/voluntario/fase1",
            "/usuarios/voluntario/fase2/**",
            "/usuarios/verificar-cadastro",
            "/consulta/consultas/todas",
            "/especialidade/**"
    };

    private static final String[] URLS_VOLUNTARIOS = {
            "/usuarios/voluntario/**",
            "/disponibilidade"
    };

    private static final String[] URLS_VALOR_SOCIAL = {
            "/pagamento/**"
    };

    private static final String[] URLS_ASSISTIDOS_E_VOLUNTARIOS = {
            "/oauth2/authorize/**",
            "/agenda/**",
            "/calendar/eventos/**"
    };

    private static final String[] URLS_ASSISTIDOS = {
            "/consulta/**"
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AutenticacaoSucessHandler autenticacaoSucessHandler) throws Exception {
        return http
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .cors(Customizer.withDefaults())
                .csrf(CsrfConfigurer<HttpSecurity>::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(URLS_ADMINISTRADORES).hasRole("ADMINISTRADOR")
                        .requestMatchers(URLS_VOLUNTARIOS).hasRole("VOLUNTARIO")
                        .requestMatchers(URLS_VALOR_SOCIAL).hasRole("VALOR_SOCIAL")
                        .requestMatchers(URLS_ASSISTIDOS).hasAnyRole("VALOR_SOCIAL", "GRATUIDADE")
                        .requestMatchers(URLS_ASSISTIDOS_E_VOLUNTARIOS).hasAnyRole("VOLUNTARIO", "VALOR_SOCIAL", "GRATUIDADE")
                        .requestMatchers(URLS_PUBLICAS).permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(autenticacaoSucessHandler)
                )
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(autenticacaoEntryPoint()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .addFilterBefore(jwtAuthenticationFilterBean(), UsernamePasswordAuthenticationFilter.class)
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
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public AutenticacaoSucessHandler autenticacaoSucessHandler(
            UsuarioRepository usuarioRepository,
            GerenciadorTokenJwt gerenciadorTokenJwt,
            UsuarioServiceImpl usuarioService,
            OAuth2AuthorizedClientService authorizedClientService,
            SecurityContextRepository securityContextRepository) {

        return new AutenticacaoSucessHandler(
                usuarioRepository,
                gerenciadorTokenJwt,
                usuarioService,
                authorizedClientService,
                securityContextRepository
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
