package org.com.imaapi.model.usuario.input;

import lombok.Data;
import org.com.imaapi.model.enums.Funcao;

@Data
public class VoluntarioDadosProfissionaisInput {
    private Funcao funcao;
    private String registroProfissional;
    private String biografiaProfissional;
}
