package org.com.imaapi.model.input;

import jakarta.persistence.*;
import org.com.imaapi.model.enums.Funcao;

import lombok.Data;

@Data
public class VoluntarioInput {
    @Enumerated(EnumType.STRING)
    private Funcao funcao;
    private Long fkUsuario;
}

