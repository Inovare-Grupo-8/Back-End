package org.com.imaapi.model.enums;

public enum Funcao {
    JURIDICA("juridica"),
    PSICOLOGIA("psicologia"),
    PSICOPEDAGOGIA("psicopedagogia"),
    ASSISTENCIA_SOCIAL("assistência social"),
    CONTABIL("contabil"),
    FINANCEIRA("financeira"),
    PEDIATRIA("pediatria"),
    FISIOTERAPIA("fisioterapia"),
    QUIROPRAXIA("quiropráxia"),
    NUTRICAO("nutrição");

    private final String value;

    Funcao(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Funcao fromValue(String value) {
        for (Funcao funcao : Funcao.values()) {
            if (funcao.value.equalsIgnoreCase(value)) {
                return funcao;
            }
        }
        throw new IllegalArgumentException("Função inválida: " + value);
    }
}