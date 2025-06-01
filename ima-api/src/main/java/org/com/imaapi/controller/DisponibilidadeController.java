package org.com.imaapi.controller;

import org.com.imaapi.service.impl.DisponibilidadeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/disponibilidade")
public class DisponibilidadeController {
    @Autowired
    private DisponibilidadeServiceImpl disponibilidadeService;

    @GetMapping("/{voluntarioId}/horarios")
    public List<LocalTime> getHorariosDisponiveis(
            @PathVariable Integer voluntarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return disponibilidadeService.buscarHorariosDisponiveis(voluntarioId, data);
    }
}
