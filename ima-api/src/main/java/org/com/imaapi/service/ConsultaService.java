package org.com.imaapi.service;

import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.model.consulta.output.AvaliacaoFeedbackOutput;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ConsultaService {
    ResponseEntity<ConsultaOutput> criarEvento(ConsultaInput consultaInput);

    ResponseEntity<List<ConsultaOutput>> getConsultasDia(String user);
    
    ResponseEntity<List<ConsultaOutput>> getConsultasSemana(String user);
    
    ResponseEntity<List<ConsultaOutput>> getConsultasMes(String user);
      ResponseEntity<List<AvaliacaoFeedbackOutput>> getAvaliacoesFeedback(String user);
    
    ResponseEntity<List<ConsultaOutput>> getConsultasRecentes(String user);
    
    ResponseEntity<AvaliacaoFeedbackOutput> adicionarFeedback(Long id, String feedback);
    
    ResponseEntity<AvaliacaoFeedbackOutput> adicionarAvaliacao(Long id, String avaliacao);
}