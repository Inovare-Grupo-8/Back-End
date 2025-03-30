package org.com.imaapi.model.Consulta.output;

import lombok.Data;
import org.com.imaapi.model.Usuario.Especialidade;
import org.com.imaapi.model.Usuario.Usuario;
import org.com.imaapi.model.enums.ModalidadeConsulta;
import org.com.imaapi.model.enums.StatusConsulta;

import java.time.LocalDateTime;

@Data
public class ConsultaOutput {
    private LocalDateTime horario;
    private StatusConsulta status;
    private ModalidadeConsulta modalidade;
    private String local;
    private String observacoes;
    private Especialidade especialidade;
    private Usuario assistido;
    private Usuario voluntario;
}
