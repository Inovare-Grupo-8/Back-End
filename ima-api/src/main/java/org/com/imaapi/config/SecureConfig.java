package org.com.imaapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecureConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/usuarios/**",       // cadastro, login normal, etc
                                "/login-google",      // o botão que redireciona pro Google
                                "/oauth2/**"          // rotas internas do Spring OAuth
                        ).permitAll()
                        .anyRequest().authenticated()  // o resto, precisa estar logado
                )
                .oauth2Login(Customizer.withDefaults());

        return http.build();
    }

}
