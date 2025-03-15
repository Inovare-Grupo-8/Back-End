package org.com.imaapi.model.enums;

public enum TipoUsuario {
    VALOR_SOCIAL("valor social"),
    GRATUIDADE("gratuidade");

    private final String value;

    TipoUsuario(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoUsuario fromValue(String value) {
        for (TipoUsuario tipo : TipoUsuario.values()) {
            if (tipo.value.equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de usuário inválido: " + value);
    }
}
