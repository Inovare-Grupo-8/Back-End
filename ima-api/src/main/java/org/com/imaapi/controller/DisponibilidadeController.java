package org.com.imaapi.controller;

import org.com.imaapi.model.usuario.Disponibilidade;
import org.com.imaapi.service.impl.DisponibilidadeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/disponibilidade")
public class DisponibilidadeController {
    @Autowired
    private DisponibilidadeServiceImpl disponibilidadeService;

    @PostMapping
    public ResponseEntity<Void> criarDisponibilidade(
            @RequestParam Integer usuarioId,
            @RequestBody Disponibilidade disponibilidade) {
        boolean criado = disponibilidadeService.criarDisponibilidade(usuarioId, disponibilidade);
        if (!criado) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(201).build();
    }

    @PatchMapping
    public ResponseEntity<Void> atualizarDisponibilidade(
            @RequestParam Integer usuarioId,
            @RequestBody Disponibilidade disponibilidade) {
        boolean atualizado = disponibilidadeService.atualizarDisponibilidade(usuarioId, disponibilidade);
        if (!atualizado) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }
}
