package org.com.imaapi.service;

import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface ConsultaService {
    public ResponseEntity<ConsultaOutput> criarEvento(ConsultaInput consultaInput);
    public ResponseEntity<Consulta> buscarEventoPorId(@PathVariable Integer id) ;
    public ResponseEntity<List<Consulta>> buscarEventosPorAssistido(@PathVariable Integer idAssistido);
    public ResponseEntity<List<Consulta>> buscarEventosPorVoluntario(@PathVariable Integer idVoluntario);
}