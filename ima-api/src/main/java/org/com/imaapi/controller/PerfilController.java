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

    @GetMapping("/voluntario/dados-pessoais")
    public ResponseEntity<UsuarioOutput> buscarDadosPessoaisVoluntario(@RequestParam Integer voluntarioId) {
        UsuarioOutput usuarioOutput = perfilService.buscarUsuarioComEnderecoPorId(voluntarioId);
        if (usuarioOutput == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuarioOutput);
    }

    @GetMapping("/assistido/dados-pessoais")
    public ResponseEntity<UsuarioOutput> buscarDadosPessoaisAssistido(@RequestParam Integer assistidoId) {
        UsuarioOutput usuarioOutput = perfilService.buscarUsuarioComEnderecoPorId(assistidoId);
        if (usuarioOutput == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuarioOutput);
    }

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