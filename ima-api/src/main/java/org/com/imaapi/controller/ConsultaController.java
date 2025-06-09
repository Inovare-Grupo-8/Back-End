package org.com.imaapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import org.com.imaapi.model.consulta.dto.ConsultaDto;
import org.com.imaapi.model.consulta.dto.ConsultaCountDto;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.input.ConsultaRemarcarInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.model.enums.ModalidadeConsulta;
import org.com.imaapi.model.enums.StatusConsulta;
import org.com.imaapi.service.ConsultaService;
import org.com.imaapi.util.JsonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/consulta")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @PostMapping
    public ResponseEntity<ConsultaOutput> criarEvento(@RequestBody @Valid ConsultaInput consultaInput) {
        return consultaService.criarEvento(consultaInput);
    }    @GetMapping("/consultas/dia")
    public ResponseEntity<ConsultaCountDto> getConsultasDia(@RequestParam String user) {
        ResponseEntity<List<ConsultaDto>> response = consultaService.getConsultasDia(user);
        if (response.getBody() != null) {
            int count = response.getBody().size();
            return ResponseEntity.ok(new ConsultaCountDto(count));
        }
        return ResponseEntity.ok(new ConsultaCountDto(0));
    }

    @GetMapping("/consultas/semana")
    public ResponseEntity<ConsultaCountDto> getConsultasSemana(@RequestParam String user) {
        ResponseEntity<List<ConsultaDto>> response = consultaService.getConsultasSemana(user);
        if (response.getBody() != null) {
            int count = response.getBody().size();
            return ResponseEntity.ok(new ConsultaCountDto(count));
        }
        return ResponseEntity.ok(new ConsultaCountDto(0));
    }

    @GetMapping("/consultas/mes")
    public ResponseEntity<ConsultaCountDto> getConsultasMes(@RequestParam String user) {
        ResponseEntity<List<ConsultaDto>> response = consultaService.getConsultasMes(user);
        if (response.getBody() != null) {
            int count = response.getBody().size();
            return ResponseEntity.ok(new ConsultaCountDto(count));
        }
        return ResponseEntity.ok(new ConsultaCountDto(0));
    }

    @GetMapping("/consultas/avaliacoes-feedback")
    public ResponseEntity<Map<String, Object>> getAvaliacoesFeedback(@RequestParam String user) {
        return consultaService.getAvaliacoesFeedback(user);
    }

    @GetMapping("/consultas/recentes")
    public ResponseEntity<List<ConsultaDto>> getConsultasRecentes(@RequestParam String user) {
        return consultaService.getConsultasRecentes(user);
    }

    @GetMapping("/consultas/{idUsuario}/proxima")
    public ResponseEntity<?> getProximaConsulta(@PathVariable Integer idUsuario) {
        return consultaService.getProximaConsulta(idUsuario);
    }

    @GetMapping("/consultas/todas")
    public ResponseEntity<List<ConsultaDto>> getTodasConsultas() {
        return consultaService.getTodasConsultas();
    }

    @GetMapping("/consultas/minhas")
    public ResponseEntity<List<ConsultaDto>> getMinhasConsultas() {
        return consultaService.getConsultasUsuarioLogado();
    }

    @GetMapping("/consultas/{id}")
    public ResponseEntity<ConsultaDto> getConsultaPorId(@PathVariable Integer id) {
        return consultaService.getConsultaPorId(id);
    }


    @PostMapping("/consultas/{id}/feedback")
    public ResponseEntity<ConsultaDto> adicionarFeedback(
            @PathVariable Integer id,
            @RequestBody String feedback) {
        return consultaService.adicionarFeedback(id, feedback);

    }

       @PostMapping("/consultas/{id}/avaliacao")

    public ResponseEntity<ConsultaDto> adicionarAvaliacao(
            @PathVariable Integer id,
            @RequestBody String avaliacao) {
        return consultaService.adicionarAvaliacao(id, avaliacao);
    }

    
    @GetMapping("/consultas/historico")
    public ResponseEntity<List<ConsultaOutput>> listarHistoricoConsultasVoluntario(
            @RequestParam("user") String user) {
        List<ConsultaOutput> historico = consultaService.buscarHistoricoConsultas(user);
        return ResponseEntity.ok(historico);
    }
    
    @GetMapping("/consultas/3-proximas")
    public ResponseEntity<List<ConsultaOutput>> listarProximasConsultas(
            @RequestParam("user") String user) {
        List<ConsultaOutput> proximasConsultas = consultaService.buscarProximasConsultas(user);
        return ResponseEntity.ok(proximasConsultas);
    }
    
    @PatchMapping("/consultas/{id}/remarcar")
    public ResponseEntity<Void> remarcarConsulta(
            @PathVariable Integer id,
            @RequestBody ConsultaRemarcarInput input) {
        consultaService.remarcarConsulta(id, input);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/cancelar/{id}")
    public ResponseEntity<ConsultaDto> cancelarConsulta(@PathVariable Integer id) {
        return consultaService.cancelarConsulta(id);
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateConsultaInput(@RequestBody String rawJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(rawJson);

            Map<String, Object> response = new HashMap<>();
            Map<String, List<String>> errors = new HashMap<>();
            boolean hasErrors = false;

            // Horário é obrigatório
            if (!jsonNode.has("horario") || jsonNode.get("horario").isNull() || jsonNode.get("horario").asText().isEmpty()) {
                errors.put("horario", Collections.singletonList("Horário é obrigatório"));
                hasErrors = true;
            } else {
                try {
                    LocalDateTime.parse(jsonNode.get("horario").asText());
                } catch (DateTimeParseException e) {
                    errors.put("horario", Collections.singletonList("Horário inválido"));
                    hasErrors = true;
                }
            }

            // Status é obrigatório e deve ser válido
            if (!jsonNode.has("status") || jsonNode.get("status").isNull() || jsonNode.get("status").asText().isEmpty()) {
                errors.put("status", Collections.singletonList("Status é obrigatório"));
                hasErrors = true;
            } else {
                try {
                    StatusConsulta.valueOf(jsonNode.get("status").asText());
                } catch (IllegalArgumentException e) {
                    errors.put("status", Collections.singletonList("Status inválido"));
                    hasErrors = true;
                }
            }

            // Verifica se a modalidade é válida
            if (!jsonNode.has("modalidade") || jsonNode.get("modalidade").isNull() || jsonNode.get("modalidade").asText().isEmpty()) {
                errors.put("modalidade", Collections.singletonList("Modalidade é obrigatória"));
                hasErrors = true;
            } else {
                try {
                    ModalidadeConsulta.valueOf(jsonNode.get("modalidade").asText());
                } catch (IllegalArgumentException e) {
                    errors.put("modalidade", Collections.singletonList("Modalidade inválida"));
                    hasErrors = true;
                }
            }

            // Local é obrigatório
            if (!jsonNode.has("local") || jsonNode.get("local").isNull() || jsonNode.get("local").asText().isEmpty()) {
                errors.put("local", Collections.singletonList("Local é obrigatório"));
                hasErrors = true;
            }

            // Verifica referências de entidade
            boolean hasEspecialidadeRef = jsonNode.has("idEspecialidade") && !jsonNode.get("idEspecialidade").isNull();
            boolean hasAssistidoRef = jsonNode.has("idAssistido") && !jsonNode.get("idAssistido").isNull();
            boolean hasVoluntarioRef = jsonNode.has("idVoluntario") && !jsonNode.get("idVoluntario").isNull();

            if (!hasEspecialidadeRef && !hasAssistidoRef && !hasVoluntarioRef) {
                errors.put("especialidade", Collections.singletonList("Pelo menos uma referência (especialidade, assistido ou voluntario) deve ser fornecida"));
                hasErrors = true;
            }

            response.put("hasErrors", hasErrors);
            if (hasErrors) {
                response.put("errors", errors);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("hasErrors", true);
            response.put("errors", Collections.singletonMap("global", Collections.singletonList("Erro ao validar input")));
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/horarios-disponiveis")
    public ResponseEntity<?> getHorariosDisponiveis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam Integer idVoluntario
    ) {
        return consultaService.getHorariosDisponiveis(data, idVoluntario);
    }


    @GetMapping("/consultas/hoje")
    public ResponseEntity<Map<String, Object>> getConsultasHoje(@RequestParam String user) {
        try {
            List<ConsultaDto> consultasDia = consultaService.getConsultasDia(user).getBody();
            if (consultasDia == null) {
                consultasDia = new ArrayList<>();
            }

            // Count appointments by status
            Map<String, Long> statusCount = consultasDia.stream()
                .collect(Collectors.groupingBy(ConsultaDto::getStatus, Collectors.counting()));

            Map<String, Object> response = new HashMap<>();
            response.put("consultas", consultasDia);
            response.put("total", consultasDia.size());
            response.put("statusCount", statusCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}