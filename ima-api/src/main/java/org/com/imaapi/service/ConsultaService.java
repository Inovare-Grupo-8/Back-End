package org.com.imaapi.service;

import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.springframework.http.ResponseEntity;

public interface ConsultaService {
    public ResponseEntity<ConsultaOutput> criarEvento(ConsultaInput consultaInput);
}
