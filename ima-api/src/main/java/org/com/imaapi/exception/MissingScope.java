package org.com.imaapi.exception;

import java.util.Set;

public record MissingScope(String error, Set<String> requiredScopes, Set<String> currentScopes, String incrementalAuthUrl) {
}
