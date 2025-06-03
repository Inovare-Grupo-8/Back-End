package org.com.imaapi.model.consulta;

import lombok.Getter;
import lombok.Lombok;
import lombok.Setter;
import org.com.imaapi.model.enums.ModalidadeConsulta;

import java.time.LocalDateTime;
@Getter
@Setter
public class RemarcarConsulta {
          private LocalDateTime horario;
        private String local;
        private ModalidadeConsulta modalidade;

    }


