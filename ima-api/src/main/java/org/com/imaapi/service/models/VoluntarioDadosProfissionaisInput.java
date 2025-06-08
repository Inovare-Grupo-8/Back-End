package org.com.imaapi.service.models;

import lombok.Data;
import java.util.List;

@Data
public class VoluntarioDadosProfissionaisInput {
    private String registroProfissional;
    private String biografiaProfissional;
    private String especialidade;
    private List<String> especialidades;
}
