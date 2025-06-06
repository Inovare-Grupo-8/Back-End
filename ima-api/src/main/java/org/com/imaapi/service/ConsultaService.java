package org.com.imaapi.service;

import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface ConsultaService {
    ResponseEntity<ConsultaOutput> criarEvento(ConsultaInput consultaInput);
    
    ResponseEntity<?> getConsultasDia(String user);
    
    ResponseEntity<?> getConsultasSemana(String user);
    
    ResponseEntity<?> getConsultasMes(String user);
    
    ResponseEntity<?> getAvaliacoesFeedback(String user);
    
    ResponseEntity<?> getConsultasRecentes(String user);

    ResponseEntity<ConsultaOutput> getProximaConsulta(Integer idUsuario);

    ResponseEntity<?> adicionarFeedback(Long id, String feedback);
    
    ResponseEntity<?> adicionarAvaliacao(Long id, String avaliacao);

    ResponseEntity<?> getHorariosDisponiveis(LocalDate data, Integer idVoluntario);
}