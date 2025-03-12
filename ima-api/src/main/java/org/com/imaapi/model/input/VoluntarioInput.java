package org.com.imaapi.model.input;

import jakarta.persistence.*;
import org.com.imaapi.model.cargo.Funcao;

import lombok.Data;

@Data
public class VoluntarioInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVoluntario;

    @Enumerated(EnumType.STRING)
    private Funcao funcao;

    private Long fkUsuario;
}

