package org.com.imaapi.controller;

import jakarta.validation.Valid;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/consulta")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @PostMapping
    public ResponseEntity<ConsultaOutput> criarEvento(@RequestBody @Valid ConsultaInput consultaInput) {
        return consultaService.criarEvento(consultaInput);
    }

    @GetMapping("/consultas/dia")
    public ResponseEntity<?> getConsultasDia(@RequestParam String user) {
        return consultaService.getConsultasDia(user);
    }

    @GetMapping("/consultas/semana")
    public ResponseEntity<?> getConsultasSemana(@RequestParam String user) {
        return consultaService.getConsultasSemana(user);
    }

    @GetMapping("/consultas/mes")
    public ResponseEntity<?> getConsultasMes(@RequestParam String user) {
        return consultaService.getConsultasMes(user);
    }

    @GetMapping("/consultas/avaliacoes-feedback")
    public ResponseEntity<?> getAvaliacoesFeedback(@RequestParam String user) {
        return consultaService.getAvaliacoesFeedback(user);
    }

    @GetMapping("/consultas/id-recentes")
    public ResponseEntity<?> getConsultasRecentes(@RequestParam String user) {
        return consultaService.getConsultasRecentes(user);
    }

    @GetMapping("/consultas/{idUsuario}/proxima")
    public ResponseEntity<?> getProximaConsulta(@PathVariable Integer idUsuario) {
        return consultaService.getProximaConsulta(idUsuario);
    }

    @PostMapping("/consultas/{id}/feedback")
    public ResponseEntity<?> adicionarFeedback(
            @PathVariable Long id,
            @RequestBody String feedback) {
        return consultaService.adicionarFeedback(id, feedback);
    }

    @PostMapping("/consultas/{id}/avaliacao")
    public ResponseEntity<?> adicionarAvaliacao(
            @PathVariable Long id,
            @RequestBody String avaliacao) {
        return consultaService.adicionarAvaliacao(id, avaliacao);
    }

    @GetMapping("/horarios-disponiveis")
    public ResponseEntity<?> getHorariosDisponiveis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam Integer idVoluntario
    ) {
        return consultaService.getHorariosDisponiveis(data, idVoluntario);
    }

}