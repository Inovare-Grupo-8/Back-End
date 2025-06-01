package org.com.imaapi.service.impl;
import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DisponibilidadeServiceImpl {

    @Autowired
    private ConsultaRepository consultaRepository;

    public List<LocalTime> buscarHorariosDisponiveis(Integer idVoluntario, LocalDate data) {
        List<LocalTime> horariosPossiveis = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            horariosPossiveis.add(LocalTime.of(h, 0));
        }

        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(23, 59, 59);
        List<Consulta> consultas = consultaRepository.findByVoluntarioAndHorarioBetween(idVoluntario, inicio, fim);

        List<LocalTime> horariosOcupados = consultas.stream()
                .map(c -> c.getHorario().toLocalTime())
                .collect(Collectors.toList());

        horariosPossiveis.removeAll(horariosOcupados);

        return horariosPossiveis;
    }
}
