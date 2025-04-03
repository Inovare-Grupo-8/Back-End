package org.com.imaapi.model.consulta.output;

import lombok.Data;
import org.com.imaapi.model.usuario.Especialidade;
import org.com.imaapi.model.usuario.Usuario;
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
