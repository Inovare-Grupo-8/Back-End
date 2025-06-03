package org.com.imaapi.service;

import org.com.imaapi.model.consulta.MotivoCancelamento;
import org.com.imaapi.model.consulta.RemarcarConsulta;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

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

}
