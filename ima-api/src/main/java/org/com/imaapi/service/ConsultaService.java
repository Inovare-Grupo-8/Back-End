package org.com.imaapi.service;

import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.springframework.http.ResponseEntity;

public interface ConsultaService {

    static ResponseEntity<ConsultaOutput> detalhar(Integer id) {
        return null;
    }

    ResponseEntity<ConsultaOutput> criarEvento(ConsultaInput consultaInput);

    ResponseEntity<ConsultaOutput> remarcar(Integer id, ConsultaInput consultaInput);

    ResponseEntity<ConsultaOutput> cancelar(Integer id);

    ResponseEntity<ConsultaOutput> buscarDetalhes(Integer id);

}
