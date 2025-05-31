package org.com.imaapi.controller;

import jakarta.validation.Valid;
import org.com.imaapi.model.usuario.input.UsuarioInput;
import org.com.imaapi.model.usuario.output.UsuarioOutput;
import org.com.imaapi.service.PerfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private PerfilService perfilService;

    @PutMapping("/voluntario/endereco")
    public ResponseEntity<Void> atualizarEnderecoVoluntario(
            @RequestParam Integer voluntarioId,
            @RequestBody @Valid UsuarioInput usuarioInput) {
        boolean atualizado = perfilService.atualizarEnderecoPorUsuarioId(
                voluntarioId, usuarioInput.getCep(), usuarioInput.getNumero(), usuarioInput.getComplemento());
        if (!atualizado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/assistido/endereco")
    public ResponseEntity<Void> atualizarEnderecoAssistido(
            @RequestParam Integer assistidoId,
            @RequestBody @Valid UsuarioInput usuarioInput) {
        boolean atualizado = perfilService.atualizarEnderecoPorUsuarioId(
                assistidoId, usuarioInput.getCep(), usuarioInput.getNumero(), usuarioInput.getComplemento());
        if (!atualizado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}