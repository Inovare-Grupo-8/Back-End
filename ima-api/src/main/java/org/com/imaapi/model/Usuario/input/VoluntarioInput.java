package org.com.imaapi.model.Usuario.input;

import jakarta.persistence.*;
import org.com.imaapi.model.Usuario.enums.Funcao;

import lombok.Data;

@Data
public class VoluntarioInput {
    @Enumerated(EnumType.STRING)
    private Funcao funcao;
    private Integer fkUsuario;
}

