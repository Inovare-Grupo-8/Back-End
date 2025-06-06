package org.com.imaapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

public class LoggingPasswordEncoder implements PasswordEncoder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingPasswordEncoder.class);
    private final PasswordEncoder delegate;
    
    public LoggingPasswordEncoder(PasswordEncoder delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public String encode(CharSequence rawPassword) {
//        LOGGER.debug("[PASSWORD_ENCODER] Codificando senha (primeiro 4 caracteres): {}...",
//                rawPassword.length() > 4 ? rawPassword.subSequence(0, 4) : "****");
        String encoded = delegate.encode(rawPassword);
//        LOGGER.debug("[PASSWORD_ENCODER] Senha codificada para hash: {}", encoded);
        return encoded;
    }
    
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
//        LOGGER.debug("[PASSWORD_ENCODER] Verificando senha (primeiro 4 caracteres): {}...",
//                rawPassword.length() > 4 ? rawPassword.subSequence(0, 4) : "****");
//        LOGGER.debug("[PASSWORD_ENCODER] Senha hash armazenada: {}", encodedPassword);
        boolean matches = delegate.matches(rawPassword, encodedPassword);
//        LOGGER.info("[PASSWORD_ENCODER] Resultado da verificação de senha: {}", matches ? "SUCESSO" : "FALHA");
        return matches;
    }
}
