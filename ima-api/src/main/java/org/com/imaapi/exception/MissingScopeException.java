package org.com.imaapi.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class MissingScopeException extends RuntimeException {
    private final Set<String> requiredScopes;
    private final Set<String> currentScopes;
    private final String incrementalAuthUrl;

    public MissingScopeException(String message, Set<String> requiredScopes, Set<String> currentScopes, String incrementalAuthUrl) {
        super(message);
        this.requiredScopes = requiredScopes;
        this.currentScopes = currentScopes;
        this.incrementalAuthUrl = incrementalAuthUrl;
    }

}
