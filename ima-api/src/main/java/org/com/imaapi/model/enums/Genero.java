package org.com.imaapi.model.enums;

public enum Genero {
    FEMININO("F"),
    MASCULINO("M");

    private final String value;

    Genero(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Genero fromValue(String value) {
        for (Genero genero : Genero.values()) {
            if (genero.value.equalsIgnoreCase(value)) {
                return genero;
            }
        }
        throw new IllegalArgumentException("Gênero inválido: " + value);
    }
}

