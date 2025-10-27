package org.com.imaapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class Oauth2ExceptionHandler {

    @ExceptionHandler(MissingScopeException.class)
    public ResponseEntity<MissingScope> handleMissingScopes(MissingScopeException ex) {
        MissingScope error = new MissingScope(
                ex.getMessage(),
                ex.getRequiredScopes(),
                ex.getCurrentScopes(),
                ex.getIncrementalAuthUrl()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
}
