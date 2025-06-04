package org.com.imaapi.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface DisponibilidadeService {
    List<LocalTime> buscarHorariosDisponiveis(Integer idUsuario, LocalDate data);
}
