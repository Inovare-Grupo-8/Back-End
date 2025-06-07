package org.com.imaapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.com.imaapi.model.consulta.dto.ConsultaDto;
import org.com.imaapi.model.consulta.MotivoCancelamento;
import org.com.imaapi.model.consulta.RemarcarConsulta;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.service.ConsultaService;
import org.com.imaapi.util.JsonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<List<ConsultaDto>> getConsultasDia(@RequestParam String user) {
        return consultaService.getConsultasDia(user);
    }

    @GetMapping("/consultas/semana")
    public ResponseEntity<List<ConsultaDto>> getConsultasSemana(@RequestParam String user) {
        return consultaService.getConsultasSemana(user);
    }

    @GetMapping("/consultas/mes")
    public ResponseEntity<List<ConsultaDto>> getConsultasMes(@RequestParam String user) {
        return consultaService.getConsultasMes(user);
    }

    @GetMapping("/consultas/avaliacoes-feedback")
    public ResponseEntity<Map<String, Object>> getAvaliacoesFeedback(@RequestParam String user) {
        return consultaService.getAvaliacoesFeedback(user);
    }

    @GetMapping("/consultas/recentes")
    public ResponseEntity<List<ConsultaDto>> getConsultasRecentes(@RequestParam String user) {
        return consultaService.getConsultasRecentes(user);
    }

    @GetMapping("/consultas/todas")
    public ResponseEntity<List<ConsultaDto>> getTodasConsultas() {
        return consultaService.getTodasConsultas();
    }@PostMapping("/consultas/{id}/feedback")
    public ResponseEntity<ConsultaDto> adicionarFeedback(
            @PathVariable Integer id,
            @RequestBody String feedback) {
        return consultaService.adicionarFeedback(id, feedback);
    }    @PostMapping("/consultas/{id}/avaliacao")
    public ResponseEntity<ConsultaDto> adicionarAvaliacao(
            @PathVariable Integer id,
            @RequestBody String avaliacao) {
        return consultaService.adicionarAvaliacao(id, avaliacao);
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateConsultaInput(@RequestBody String rawJson) {
        Map<String, Object> response = new HashMap<>();

        // First validate the basic JSON structure
        if (!JsonValidator.isValidJson(rawJson)) {
            response.put("status", "invalid");
            response.put("error", "JsonSyntaxError");
            response.put("message", JsonValidator.getJsonErrors(rawJson));
            return ResponseEntity.badRequest().body(response);
        }

        // If JSON is valid, validate against our DTO
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Configure mapper for proper handling of dates and enums
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // Parse into our DTO to validate structure
            ConsultaInput consultaInput = mapper.readValue(rawJson, ConsultaInput.class);

            // Perform business validation checks
            List<String> validationErrors = new ArrayList<>();

            // Check for required fields
            if (consultaInput.getHorario() == null) {
                validationErrors.add("O campo 'horario' é obrigatório");
            }
            if (consultaInput.getStatus() == null) {
                validationErrors.add("O campo 'status' é obrigatório");
            }
            if (consultaInput.getModalidade() == null) {
                validationErrors.add("O campo 'modalidade' é obrigatório");
            }
            if (consultaInput.getLocal() == null || consultaInput.getLocal().trim().isEmpty()) {
                validationErrors.add("O campo 'local' é obrigatório");
            }

            // Check for entity references - either ID or object must be present
            boolean hasEspecialidadeRef = consultaInput.getIdEspecialidade() != null ||
                                         (consultaInput.getEspecialidade() != null &&
                                          consultaInput.getEspecialidade().getId() != null);
            if (!hasEspecialidadeRef) {
                validationErrors.add("Informe 'idEspecialidade' ou 'especialidade' com ID válido");
            }

            boolean hasAssistidoRef = consultaInput.getIdAssistido() != null ||
                                     (consultaInput.getAssistido() != null &&
                                      consultaInput.getAssistido().getIdUsuario() != null);
            if (!hasAssistidoRef) {
                validationErrors.add("Informe 'idAssistido' ou 'assistido' com ID válido");
            }

            boolean hasVoluntarioRef = consultaInput.getIdVoluntario() != null ||
                                      (consultaInput.getVoluntario() != null &&
                                       consultaInput.getVoluntario().getIdUsuario() != null);
            if (!hasVoluntarioRef) {
                validationErrors.add("Informe 'idVoluntario' ou 'voluntario' com ID válido");
            }

            // Return validation results
            if (validationErrors.isEmpty()) {
                response.put("status", "valid");
                response.put("message", "JSON é válido e pode ser processado");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "invalid");
                response.put("error", "ValidationError");
                response.put("message", "Existem erros de validação no JSON");
                response.put("errors", validationErrors);
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            response.put("status", "invalid");
            response.put("error", e.getClass().getSimpleName());

            if (e.getMessage() != null && e.getMessage().contains("Unexpected end of")) {
                response.put("message", "JSON incompleto. Verifique se todas as chaves e colchetes foram fechados corretamente.");
            } else {
                response.put("message", e.getMessage());
            }

            return ResponseEntity.badRequest().body(response);
        }
    }
}