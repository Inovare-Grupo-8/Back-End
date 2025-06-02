package org.com.imaapi.controller;

import jakarta.validation.Valid;
import org.com.imaapi.model.usuario.input.EnderecoInput;
import org.com.imaapi.model.usuario.input.UsuarioInputAtualizacaoDadosPessoais;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.model.usuario.output.UsuarioDadosPessoaisOutput;
import org.com.imaapi.model.usuario.output.UsuarioOutput;
import org.com.imaapi.service.PerfilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private PerfilService perfilService;

    @GetMapping("/{tipo}/dados-pessoais")
    public ResponseEntity<UsuarioDadosPessoaisOutput> buscarDadosPessoais(
            @RequestParam Integer usuarioId, @PathVariable String tipo) {
        UsuarioDadosPessoaisOutput usuarioOutput = perfilService.buscarDadosPessoaisPorId(usuarioId);
        if (usuarioOutput == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuarioOutput);
    }

    @PatchMapping("/{tipo}/dados-pessoais")
    public ResponseEntity<UsuarioOutput> atualizarDadosPessoais(
            @RequestParam Integer usuarioId,
            @PathVariable String tipo,
            @RequestBody UsuarioInputAtualizacaoDadosPessoais usuarioInputAtualizacaoDadosPessoais) {
        UsuarioOutput usuarioOutput = perfilService.atualizarDadosPessoais(usuarioId, usuarioInputAtualizacaoDadosPessoais);
        if (usuarioOutput == null) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(usuarioOutput);
    }

    @GetMapping("/{tipo}/endereco")
    public ResponseEntity<EnderecoOutput> buscarEndereco(
            @RequestParam Integer usuarioId, @PathVariable String tipo) {
        EnderecoOutput enderecoOutput = perfilService.buscarEnderecoPorId(usuarioId);
        if (enderecoOutput == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(enderecoOutput);
    }

    @PutMapping("/{tipo}/endereco")
    public ResponseEntity<Void> atualizarEndereco(
            @RequestParam Integer usuarioId,
            @PathVariable String tipo,
            @RequestBody @Valid EnderecoInput enderecoInput) {
        boolean atualizado = perfilService.atualizarEnderecoPorUsuarioId(
                usuarioId, enderecoInput.getCep(), enderecoInput.getNumero(), enderecoInput.getComplemento());
        if (!atualizado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}