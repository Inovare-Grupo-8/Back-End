package org.com.imaapi.model.GoogleCalendar;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventoDTO {
    private String titulo;
    private String descricao;
    private LocalDateTime inicio;
    private LocalDateTime fim;
}
