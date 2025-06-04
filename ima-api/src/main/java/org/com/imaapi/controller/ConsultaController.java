package org.com.imaapi.controller;

import jakarta.validation.Valid;
import org.com.imaapi.model.consulta.MotivoCancelamento;
import org.com.imaapi.model.consulta.RemarcarConsulta;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/consulta")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @PatchMapping("/{id}/acao")
    public ResponseEntity<ConsultaOutput> executarAcao(
            @PathVariable Integer id,
            @RequestParam String acao,
            @RequestBody(required = false) @Valid ConsultaInput consultaInput
    ) {
        if (acao.equalsIgnoreCase("REMARCAR")) {
            return consultaService.remarcar(id, consultaInput);
        } else if (acao.equalsIgnoreCase("CANCELAR")) {
            return consultaService.cancelar(id);
        } else if (acao.equalsIgnoreCase("DETALHAR")) {
            return consultaService.buscarDetalhes(id);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaOutput> buscarPorId(@PathVariable Integer id) {
        return consultaService.buscarDetalhes(id);
    }
    @GetMapping("/historico")
    public ResponseEntity<List<ConsultaOutput>> listarHistoricoPorAssistido(@RequestParam("user") Integer idAssistido) {
        List<ConsultaOutput> historico = consultaService.listarHistoricoPorAssistido(idAssistido);
        return ResponseEntity.ok(historico);
    }

    @PostMapping("/{id}/motivo-cancelamento")
    public ResponseEntity<Void> registrarMotivoCancelamento(
            @PathVariable Integer id,
            @RequestBody @Valid MotivoCancelamento motivoCancelamento) {
        return consultaService.registrarMotivoCancelamento(id, motivoCancelamento);
    }

    @GetMapping("/agenda/dia")
    public ResponseEntity<List<ConsultaOutput>> listarConsultasPorDia(
            @RequestParam Integer user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        List<ConsultaOutput> consultas = consultaService.listarConsultasPorDia(user, data.atStartOfDay());
        return ResponseEntity.ok(consultas);
    }
    @GetMapping("/consultas/historico")
    public ResponseEntity<List<ConsultaOutput>> listarHistoricoConsultas(@RequestParam Integer user) {
        List<ConsultaOutput> historico = consultaService.listarHistoricoConsultas(user);
        return ResponseEntity.ok(historico);
    }
    @GetMapping("/historico/{consultaId}")
    public ResponseEntity<ConsultaOutput> buscarConsultaPorIdEVoluntario(
            @PathVariable Integer consultaId,
            @RequestParam Integer user) {
        ConsultaOutput consulta = consultaService.buscarConsultaPorIdEVoluntario(consultaId, user);
        return ResponseEntity.ok(consulta);
    }
    @GetMapping("/consultas/3-proximas")
    public ResponseEntity<List<ConsultaOutput>> listarProximasConsultas(@RequestParam Integer user) {
        List<ConsultaOutput> proximasConsultas = consultaService.listarProximasConsultas(user);
        return ResponseEntity.ok(proximasConsultas);
    }

    @GetMapping("/historico/{consultaId}")
    public ResponseEntity<ConsultaOutput> buscarConsultaPorIdEAssistido(
            @PathVariable Integer consultaId,
            @RequestParam Integer user) {
        ConsultaOutput consulta = consultaService.buscarConsultaPorIdEAssistido(consultaId, user);
        return ResponseEntity.ok(consulta);
    }
    @GetMapping("/consultas/3-proximas")
    public ResponseEntity<List<ConsultaOutput>> listarProximasConsultasAssistido(@RequestParam Integer user) {
        List<ConsultaOutput> proximasConsultas = consultaService.listarProximasConsultasAssistido(user);
        return ResponseEntity.ok(proximasConsultas);
    }

    @PatchMapping("/consultas/{id}/remarcar")
    public ResponseEntity<ConsultaOutput> remarcarConsulta(
            @PathVariable Integer id,
            @RequestBody RemarcarConsulta dto) {
        ConsultaOutput consultaAtualizada = consultaService.remarcarConsulta(id, dto);
        return ResponseEntity.ok(consultaAtualizada);
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
}