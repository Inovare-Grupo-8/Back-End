package org.com.imaapi.controller;

import jakarta.validation.Valid;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
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
    public ResponseEntity<ConsultaOutput> criarEvento(@RequestBody @Valid ConsultaInput consultaInput) {
        return consultaService.criarEvento(consultaInput);
    }
}