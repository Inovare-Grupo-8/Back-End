package org.com.imaapi.service;

import org.com.imaapi.model.consulta.dto.ConsultaDto;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ConsultaService {
    ResponseEntity<ConsultaOutput> criarEvento(ConsultaInput consultaInput);

    ResponseEntity<List<ConsultaDto>> getConsultasDia(String user);

    ResponseEntity<List<ConsultaDto>> getConsultasSemana(String user);

    ResponseEntity<List<ConsultaDto>> getConsultasMes(String user);

    ResponseEntity<Map<String, Object>> getAvaliacoesFeedback(String user);

    ResponseEntity<List<ConsultaDto>> getConsultasRecentes(String user);

    ResponseEntity<ConsultaDto> adicionarFeedback(Integer id, String feedback);

    ResponseEntity<ConsultaDto> adicionarAvaliacao(Integer id, String avaliacao);
    
    ResponseEntity<List<ConsultaDto>> getTodasConsultas();
}