package org.com.imaapi.controller;

import org.com.imaapi.model.Consulta.input.ConsultaInput;
import org.com.imaapi.model.Consulta.output.ConsultaOutput;
import org.com.imaapi.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consulta")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @PostMapping
    public ResponseEntity<ConsultaOutput> criarEvento(@RequestBody ConsultaInput consultaInput) {
        return consultaService.criarEvento(consultaInput);
    }
}