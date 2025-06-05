package org.com.imaapi.config;

import org.com.imaapi.service.impl.AutenticacaoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AutenticacaoProvider implements AuthenticationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutenticacaoProvider.class);

    private final AutenticacaoServiceImpl autenticacaoService;
    private final PasswordEncoder passwordEncoder;

    public AutenticacaoProvider(AutenticacaoServiceImpl autenticacaoService, PasswordEncoder passwordEncoder) {
        this.autenticacaoService = autenticacaoService;
        this.passwordEncoder = passwordEncoder;
    }    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();
        
//        LOGGER.info("[AUTENTICAR_PROVIDER] Tentando autenticar usuário: {}", username);
        
        try {
            UserDetails userDetails = this.autenticacaoService.loadUserByUsername(username);
//            LOGGER.debug("[AUTENTICAR_PROVIDER] UserDetails carregado para: {}", username);
//            LOGGER.debug("[AUTENTICAR_PROVIDER] Senha fornecida (primeiros 4 caracteres): {}",
//                    password.length() > 4 ? password.substring(0, 4) + "..." : "***");
//            LOGGER.debug("[AUTENTICAR_PROVIDER] Senha armazenada (hash): {}", userDetails.getPassword());
            
            boolean matches = this.passwordEncoder.matches(password, userDetails.getPassword());
//            LOGGER.info("[AUTENTICAR_PROVIDER] Resultado da verificação de senha para {}: {}", username, matches ? "SUCESSO" : "FALHA");
            
            if (matches) {
                LOGGER.info("[AUTENTICAR_PROVIDER] Autenticação bem-sucedida para: {}", username);
                return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            } else {
                LOGGER.warn("[AUTENTICAR_PROVIDER] Senha inválida para usuário: {}", username);
                throw new BadCredentialsException("Usuário ou senha inválidos");
            }
        } catch (Exception e) {
            LOGGER.error("[AUTENTICAR_PROVIDER] Erro durante autenticação para {}: {}", username, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
