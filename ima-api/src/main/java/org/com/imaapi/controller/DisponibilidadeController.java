package org.com.imaapi.controller;


import org.com.imaapi.model.usuario.Voluntario;
import org.com.imaapi.service.impl.DisponibilidadeServiceImpl;
import org.com.imaapi.service.impl.VoluntarioServiceImpl;
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
    private static DisponibilidadeServiceImpl disponibilidadeService;

    @Autowired
    private VoluntarioServiceImpl voluntarioService;

    @GetMapping("/{idVoluntario}/horarios")
    public List<LocalTime> getHorariosDisponiveis(
            @PathVariable Integer idVoluntario,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return disponibilidadeService.buscarHorariosDisponiveis(idVoluntario, data);
    }

    @GetMapping("/{idVoluntario}/tipos")
    public String getTiposDoVoluntario(@PathVariable Integer idVoluntario) {
        Voluntario voluntario = voluntarioService.buscarPorId(idVoluntario);
        if (voluntario == null) {
            return "Voluntário não encontrado";
        }
        // Retorna o valor do enum Funcao (ex: "juridica", "psicologia", etc)
        return voluntario.getFuncao().getValue();
    }
}
