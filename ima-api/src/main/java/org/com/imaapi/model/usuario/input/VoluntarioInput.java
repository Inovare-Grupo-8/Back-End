package org.com.imaapi.model.usuario.input;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.com.imaapi.model.enums.Funcao;

@Data
public class VoluntarioInput {    @Enumerated(EnumType.STRING)
    private Funcao funcao;
    private Integer fkUsuario;
}

