package org.com.imaapi.service;

import org.com.imaapi.model.consulta.dto.ConsultaDto;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.input.ConsultaRemarcarInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import java.time.LocalDate;

public interface ConsultaService {
    ResponseEntity<ConsultaOutput> criarEvento(ConsultaInput consultaInput);

    ResponseEntity<List<ConsultaDto>> getConsultasDia(String user);

    ResponseEntity<List<ConsultaDto>> getConsultasSemana(String user);

    ResponseEntity<List<ConsultaDto>> getConsultasMes(String user);

    ResponseEntity<Map<String, Object>> getAvaliacoesFeedback(String user);

    ResponseEntity<List<ConsultaDto>> getConsultasRecentes(String user);

    ResponseEntity<ConsultaDto> adicionarFeedback(Integer id, String feedback);

    ResponseEntity<ConsultaDto> adicionarAvaliacao(Integer id, String avaliacao);

    ResponseEntity<ConsultaOutput> getProximaConsulta(Integer idUsuario);

    ResponseEntity<?> getHorariosDisponiveis(LocalDate data, Integer idVoluntario);

    ResponseEntity<List<ConsultaDto>> getTodasConsultas();

    ResponseEntity<List<ConsultaDto>> getConsultasUsuarioLogado();

    public ResponseEntity<ConsultaDto> getConsultaPorId(Integer id);
    
    // Novos métodos
    List<ConsultaOutput> buscarConsultasPorDia(String user, LocalDate data);
    
    List<ConsultaOutput> buscarHistoricoConsultas(String user);
    
    ConsultaOutput buscarConsultaPorIdEUsuario(Integer consultaId, String user);
    
    List<ConsultaOutput> buscarProximasConsultas(String user);
    
    void remarcarConsulta(Integer id, ConsultaRemarcarInput input);
    
    // Cancel consultation method
    ResponseEntity<ConsultaDto> cancelarConsulta(Integer id);
}