package org.com.imaapi.controller;

<<<<<<< HEAD
import org.com.imaapi.model.usuario.Voluntario;
import org.com.imaapi.service.impl.DisponibilidadeServiceImpl;
import org.com.imaapi.service.impl.VoluntarioServiceImpl;
=======
import org.com.imaapi.service.impl.DisponibilidadeServiceImpl;
>>>>>>> 9c893cfce19f60122119fdbd6957eacb2bb78e1c
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

<<<<<<< HEAD
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
=======
    @GetMapping("/{voluntarioId}/horarios")
    public List<LocalTime> getHorariosDisponiveis(
            @PathVariable Integer voluntarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return disponibilidadeService.buscarHorariosDisponiveis(voluntarioId, data);
>>>>>>> 9c893cfce19f60122119fdbd6957eacb2bb78e1c
    }
}
