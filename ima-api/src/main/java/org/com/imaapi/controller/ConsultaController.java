package org.com.imaapi.controller;

import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consulta")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @PostMapping
    public ResponseEntity<ConsultaOutput> criarEvento(@RequestBody ConsultaInput consultaInput) {
        return consultaService.criarEvento(consultaInput);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consulta> buscarEventoPorId(@PathVariable Integer id) {
        return consultaService.buscarEventoPorId(id);
    }

    @GetMapping("/assistido/{idAssistido}")
    public ResponseEntity<List<Consulta>> buscarEventosPorAssistido(@PathVariable Integer idAssistido) {
        return consultaService.buscarEventosPorAssistido(idAssistido);
    }

    @GetMapping("/voluntario/{idVoluntario}")
    public ResponseEntity<List<Consulta>> buscarEventosPorVoluntario(@PathVariable Integer idVoluntario) {
        return consultaService.buscarEventosPorVoluntario(idVoluntario);
    }
}