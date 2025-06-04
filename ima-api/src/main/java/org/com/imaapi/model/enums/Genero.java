package org.com.imaapi.model.enums;

public enum Genero {
    M,
    F,
    OUTRO;

    public static Genero fromString(String valor) {
        if (valor == null) return null;

        switch (valor.trim().toUpperCase()) {
            case "M":
            case "MASCULINO":
                return M;
            case "F":
            case "FEMININO":
                return F;
            case "OUTRO":
            case "O":
                return OUTRO;
            default:
                throw new IllegalArgumentException("Gênero inválido: " + valor);
        }
    }
}
