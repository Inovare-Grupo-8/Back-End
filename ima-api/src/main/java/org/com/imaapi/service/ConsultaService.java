package org.com.imaapi.service;

import org.com.imaapi.model.consulta.MotivoCancelamento;
import org.com.imaapi.model.consulta.RemarcarConsulta;
import org.com.imaapi.model.consulta.dto.ConsultaDto;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import java.util.List;
import java.util.Map;

public interface ConsultaService {

    default ResponseEntity<ConsultaOutput> detalhar(Integer id) {
        return null;
    }

    ResponseEntity<ConsultaOutput> criarEvento(ConsultaInput consultaInput);

    ResponseEntity<ConsultaOutput> remarcar(Integer id, ConsultaInput consultaInput);

    ResponseEntity<ConsultaOutput> cancelar(Integer id);

    ResponseEntity<ConsultaOutput> buscarDetalhes(Integer id);

    List<ConsultaOutput> listarHistoricoPorAssistido(Integer idAssistido);

    ResponseEntity<Void> registrarMotivoCancelamento(Integer id, MotivoCancelamento motivoCancelamento);

    List<ConsultaOutput> listarConsultasPorDia(Integer voluntarioId, LocalDateTime data);

    List<ConsultaOutput> listarHistoricoConsultas(Integer voluntarioId);

    ConsultaOutput buscarConsultaPorIdEVoluntario(Integer consultaId, Integer voluntarioId);

    List<ConsultaOutput> listarProximasConsultas(Integer voluntarioId);

    ConsultaOutput buscarConsultaPorIdEAssistido(Integer consultaId, Integer assistidoId);

    List<ConsultaOutput> listarProximasConsultasAssistido(Integer assistidoId);

    public ConsultaOutput remarcarConsulta(Integer id, RemarcarConsulta dto);

    ResponseEntity<ConsultaOutput> criarEvento(ConsultaInput consultaInput);

    ResponseEntity<?> getConsultasDia(String user);

    ResponseEntity<?> getConsultasSemana(String user);

    ResponseEntity<?> getConsultasMes(String user);

    ResponseEntity<?> getAvaliacoesFeedback(String user);

    ResponseEntity<?> getConsultasRecentes(String user);

    ResponseEntity<?> adicionarFeedback(Long id, String feedback);

    ResponseEntity<?> adicionarAvaliacao(Long id, String avaliacao);
    ResponseEntity<ConsultaOutput> criarEvento(ConsultaInput consultaInput);

    ResponseEntity<List<ConsultaDto>> getConsultasDia(String user);

    ResponseEntity<List<ConsultaDto>> getConsultasSemana(String user);

    ResponseEntity<List<ConsultaDto>> getConsultasMes(String user);

    ResponseEntity<Map<String, Object>> getAvaliacoesFeedback(String user);

    ResponseEntity<List<ConsultaDto>> getConsultasRecentes(String user);

    ResponseEntity<ConsultaDto> adicionarFeedback(Integer id, String feedback);

    ResponseEntity<ConsultaDto> adicionarAvaliacao(Integer id, String avaliacao);

    ResponseEntity<List<ConsultaDto>> getTodasConsultas();

    ResponseEntity<?> getConsultasDia(String user);

    ResponseEntity<?> getConsultasSemana(String user);

    ResponseEntity<?> getConsultasMes(String user);

    ResponseEntity<?> getAvaliacoesFeedback(String user);

    ResponseEntity<?> getConsultasRecentes(String user);

    ResponseEntity<?> adicionarFeedback(Long id, String feedback);

    ResponseEntity<?> adicionarAvaliacao(Long id, String avaliacao);
}