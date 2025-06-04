package org.com.imaapi.model.consulta.output;

import lombok.Data;
import org.com.imaapi.model.usuario.Especialidade;
import org.com.imaapi.model.enums.ModalidadeConsulta;
import org.com.imaapi.model.enums.StatusConsulta;

import java.time.LocalDateTime;

@Data
public class ConsultaOutput {
    // Informações da Consulta
    private Long idConsulta;
    private LocalDateTime horario;
    private StatusConsulta status;
    private ModalidadeConsulta modalidade;
    private String local;
    private String observacoes;
    private String feedbackStatus;
    private String avaliacaoStatus;

    private VoluntarioInfo voluntario;

    private AssistidoInfo assistido;

    private EspecialidadeInfo especialidade;

    @Data
    public static class VoluntarioInfo {
        private Long id;
        private String nome;
        private String sobrenome;
        private String email;
        private String telefone;
    }

    @Data
    public static class AssistidoInfo {
        private Long id;
        private String nome;
        private String sobrenome;
        private String email;
        private String telefone;
    }

    @Data
    public static class EspecialidadeInfo {
        private Long id;
        private String nome;
        private String descricao;
    }
}
