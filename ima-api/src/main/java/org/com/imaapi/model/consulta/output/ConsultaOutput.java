package org.com.imaapi.model.consulta.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.com.imaapi.model.enums.ModalidadeConsulta;
import org.com.imaapi.model.enums.StatusConsulta;
import org.com.imaapi.model.especialidade.Especialidade;
import org.com.imaapi.model.usuario.Usuario;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsultaOutput {
    private LocalDateTime horario;
    private StatusConsulta status;
    private ModalidadeConsulta modalidade;
    private String local;
    private String observacoes;
    private Especialidade especialidade;
    private Usuario assistido;
    private Usuario voluntario;
    private Integer idConsulta;

    public ConsultaOutput(Integer idConsulta, LocalDateTime horario, StatusConsulta status, ModalidadeConsulta modalidade, String local, String observacoes, Especialidade especialidade, Usuario assistido, Usuario voluntario) {
    }

    public ConsultaOutput() {

    }
}
