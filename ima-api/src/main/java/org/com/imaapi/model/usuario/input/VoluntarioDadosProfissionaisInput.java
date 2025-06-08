package org.com.imaapi.model.usuario.input;

import lombok.Data;
import org.com.imaapi.model.enums.Funcao;

import java.util.List;

@Data
public class VoluntarioDadosProfissionaisInput {
    private Funcao funcao;
    private String registroProfissional;
    private String biografiaProfissional;
    private String especialidade; 
    private List<String> especialidades;
}
