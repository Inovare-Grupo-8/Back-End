package org.com.imaapi.model.consulta.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.com.imaapi.model.enums.ModalidadeConsulta;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsultaRemarcarInput {
    private LocalDateTime novoHorario;
    private ModalidadeConsulta modalidade;
    private String local;
    private String observacoes;
}
