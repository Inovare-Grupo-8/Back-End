package org.com.imaapi.model.consulta;

import jakarta.validation.constraints.NotBlank;

public class MotivoCancelamento {

    @NotBlank(message = "Motivo não pode ser vazio")
    private String motivo;

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

}
