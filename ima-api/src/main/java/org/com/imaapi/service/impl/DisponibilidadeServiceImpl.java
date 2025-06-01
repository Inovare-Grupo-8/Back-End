package org.com.imaapi.service.impl;
import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.repository.ConsultaRepository;
<<<<<<< HEAD
=======
import org.com.imaapi.service.DisponibilidadeService;
>>>>>>> 9c893cfce19f60122119fdbd6957eacb2bb78e1c
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
<<<<<<< HEAD
public class DisponibilidadeServiceImpl {
=======
public class DisponibilidadeServiceImpl implements DisponibilidadeService {
>>>>>>> 9c893cfce19f60122119fdbd6957eacb2bb78e1c

    @Autowired
    private ConsultaRepository consultaRepository;

<<<<<<< HEAD
    public List<LocalTime> buscarHorariosDisponiveis(Integer idVoluntario, LocalDate data) {
=======
    public List<LocalTime> buscarHorariosDisponiveis(Integer idUsuario, LocalDate data) {
>>>>>>> 9c893cfce19f60122119fdbd6957eacb2bb78e1c
        List<LocalTime> horariosPossiveis = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            horariosPossiveis.add(LocalTime.of(h, 0));
        }

        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(23, 59, 59);
<<<<<<< HEAD
        List<Consulta> consultas = consultaRepository.findByVoluntarioAndHorarioBetween(idVoluntario, inicio, fim);
=======
        List<Consulta> consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioBetween(idUsuario, inicio, fim);
>>>>>>> 9c893cfce19f60122119fdbd6957eacb2bb78e1c

        List<LocalTime> horariosOcupados = consultas.stream()
                .map(c -> c.getHorario().toLocalTime())
                .collect(Collectors.toList());

        horariosPossiveis.removeAll(horariosOcupados);

        return horariosPossiveis;
    }
}
