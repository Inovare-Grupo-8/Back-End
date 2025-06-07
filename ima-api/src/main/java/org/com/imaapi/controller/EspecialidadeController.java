package org.com.imaapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.com.imaapi.model.especialidade.dto.EspecialidadeDto;
import org.com.imaapi.service.EspecialidadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/especialidade")
@RequiredArgsConstructor
public class EspecialidadeController {

    private final EspecialidadeService especialidadeService;

    @PostMapping
    @Operation(summary = "Criar uma nova especialidade")
    public ResponseEntity<EspecialidadeDto> criar(@RequestBody EspecialidadeDto especialidadeDto) {
        return especialidadeService.criar(especialidadeDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma especialidade existente")
    public ResponseEntity<EspecialidadeDto> atualizar(@PathVariable Integer id, @RequestBody EspecialidadeDto especialidadeDto) {
        return especialidadeService.atualizar(id, especialidadeDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar uma especialidade por ID")
    public ResponseEntity<EspecialidadeDto> buscarPorId(@PathVariable Integer id) {
        return especialidadeService.buscarPorId(id);
    }

    @GetMapping
    @Operation(summary = "Listar todas as especialidades")
    public ResponseEntity<List<EspecialidadeDto>> listarTodas() {
        return especialidadeService.listarTodas();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma especialidade")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        return especialidadeService.deletar(id);
    }
}
