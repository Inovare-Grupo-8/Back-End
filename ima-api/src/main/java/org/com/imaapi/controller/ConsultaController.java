package org.com.imaapi.controller;

import jakarta.validation.Valid;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consulta")
public class ConsultaController {
    @PatchMapping("/{id}/acao")
    public ResponseEntity<ConsultaOutput> executarAcao(
            @PathVariable Integer id,
            @RequestParam String acao,
            @RequestBody(required = false) @Valid ConsultaInput consultaInput
    ) {
        if (acao.equalsIgnoreCase("REMARCAR")) {
            return ConsultaService.remarcar(id, consultaInput);
        } else if (acao.equalsIgnoreCase("CANCELAR")) {
            return ConsultaService.cancelar(id);
        } else if (acao.equalsIgnoreCase("DETALHAR")) {
            return ConsultaService.detalhar(id);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}