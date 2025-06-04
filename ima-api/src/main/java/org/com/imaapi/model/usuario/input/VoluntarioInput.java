package org.com.imaapi.model.usuario.input;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.com.imaapi.model.enums.Funcao;

import lombok.Data;

@Data
public class VoluntarioInput {
    @Enumerated(EnumType.STRING)
    private Funcao funcao;
    private Integer fkUsuario;
}

