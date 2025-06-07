package org.com.imaapi.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoUsuario {
    ADMINISTRADOR("administrador"),
    VOLUNTARIO("voluntario"),
    VALOR_SOCIAL("valor social"),
    NAO_CLASSIFICADO("não classificado"),
    GRATUIDADE("gratuidade");

    private final String value;

    TipoUsuario(String value) {
        this.value = value;
    }    public String getValue() {
        return value;
    }

    @JsonValue
    public String getJsonValue() {
        return this.name(); // Return enum name for JSON serialization
    }

    public String getPrefixed() {
        return "ROLE_" + this.name();
    }    public static TipoUsuario fromValue(String value) {
        for (TipoUsuario tipo : TipoUsuario.values()) {
            if (tipo.value.equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de usuário inválido: " + value);
    }

    @JsonCreator
    public static TipoUsuario fromJson(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de usuário não pode ser nulo ou vazio");
        }
        
        // First try to match by enum name (for compatibility with frontend)
        for (TipoUsuario tipo : TipoUsuario.values()) {
            if (tipo.name().equalsIgnoreCase(input.trim())) {
                return tipo;
            }
        }
        
        // Then try to match by value (for display value matching)
        for (TipoUsuario tipo : TipoUsuario.values()) {
            if (tipo.value.equalsIgnoreCase(input.trim())) {
                return tipo;
            }
        }
        
        throw new IllegalArgumentException("Tipo de usuário inválido: " + input + 
            ". Valores aceitos: " + java.util.Arrays.toString(TipoUsuario.values()));
    }
}
