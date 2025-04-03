package org.com.imaapi.model.consulta.input;

import lombok.Data;
import org.com.imaapi.model.enums.ModalidadeConsulta;
import org.com.imaapi.model.enums.StatusConsulta;

import java.time.LocalDateTime;

@Data
public class ConsultaInput {
    private LocalDateTime horario;
    private StatusConsulta status;
    private ModalidadeConsulta modalidade;
    private String local;
    private String observacoes;
    private Integer idEspecialidade;
    private Integer idAssistido;
    private Integer idVoluntario;
}
