package org.com.imaapi.model.enums;

public enum Genero {
    MASCULINO("MASCULINO"),
    FEMININO("FEMININO"),
    OUTRO("OUTRO");

    private final String value;

    Genero(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Genero fromString(String valor) {
        if (valor == null) return null;

        switch (valor.trim().toUpperCase()) {
            case "M":
            case "MASCULINO":
                return MASCULINO;
            case "F":
            case "FEMININO":
                return FEMININO;
            case "OUTRO":
            case "O":
                return OUTRO;
            default:
                throw new IllegalArgumentException("Gênero inválido: " + valor);
        }
    }
}
