package org.com.imaapi.controller;
import jakarta.validation.Valid;
import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.service.ConsultaService;
import org.com.imaapi.service.impl.ConsultaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consulta")
public class ConsultaController {

    @Autowired
    private static ConsultaService consultaService;

    @PostMapping
    public ResponseEntity<ConsultaOutput> criarEvento(@RequestBody @Valid ConsultaInput consultaInput) {
        return consultaService.criarEvento(consultaInput);
    }

    @GetMapping("/proxima")
    public List<Consulta> getProximasConsultas(
            @RequestParam String user,
            @RequestParam(defaultValue = "1") int limit,
            @RequestParam Integer idUsuario
    ) {
        if ("assistido".equalsIgnoreCase(user)) {
            return consultaService.buscarProximasConsultasAssistido(idUsuario, limit);
        } else if ("voluntario".equalsIgnoreCase(user)) {
            return consultaService.buscarProximasConsultasVoluntario(idUsuario, limit);
        }
        return List.of();
    }
}
