package org.com.imaapi.model.Usuario.enums;

public enum StatusConsulta {
    AGENDADA("agendada"),
    REAGENDADA("reagendada"),
    REALIZADA("realizada");

    private final String value;

    StatusConsulta(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StatusConsulta fromValue(String value) {
        for (StatusConsulta status : StatusConsulta.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de consulta inválido: " + value);
    }
}
