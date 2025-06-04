package org.com.imaapi.model.consulta.output;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AvaliacaoFeedbackOutput {
    private ConsultaBasicaInfo consulta;
    private FeedbackInfo feedback;
    private AvaliacaoInfo avaliacao;

    @Data
    public static class ConsultaBasicaInfo {
        private Long idConsulta;
        private LocalDateTime horario;
        private String especialidadeNome;
    }

    @Data
    public static class FeedbackInfo {
        private Long id;
        private String comentario;
        private LocalDateTime dtFeedback;
        private String status;
    }

    @Data
    public static class AvaliacaoInfo {
        private Long id;
        private Integer nota;
        private LocalDateTime dtAvaliacao;
        private String status;
    }
}
