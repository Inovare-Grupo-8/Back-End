package org.com.imaapi.controller;

import jakarta.validation.Valid;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.Voluntario;
import org.com.imaapi.model.usuario.input.EnderecoInput;
import org.com.imaapi.model.usuario.input.UsuarioInputAtualizacaoDadosPessoais;
import org.com.imaapi.model.usuario.input.VoluntarioDadosProfissionaisInput;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.model.usuario.output.UsuarioDadosPessoaisOutput;
import org.com.imaapi.model.usuario.output.UsuarioOutput;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.PerfilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.com.imaapi.service.AssistenteSocialService;
import org.com.imaapi.model.usuario.output.AssistenteSocialOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private PerfilService perfilService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AssistenteSocialService assistenteSocialService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PerfilController.class);

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

    @PostMapping("/{tipo}/foto")
    public ResponseEntity<?> uploadFoto(
            @RequestParam Integer usuarioId,
            @PathVariable String tipo,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("O arquivo não pode estar vazio.");
        }

        // Validação do tipo MIME
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body("O arquivo deve ser uma imagem.");
        }

        try {
            String fotoUrl = perfilService.salvarFoto(usuarioId, tipo, file);
            return ResponseEntity.ok(Map.of("message", "Foto salva com sucesso.", "url", fotoUrl));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erro ao salvar a foto.");
        }
    }

    @PatchMapping("/voluntario/dados-profissionais")
    public ResponseEntity<Void> atualizarDadosProfissionais(
            @RequestParam Integer usuarioId,
            @RequestBody @Valid VoluntarioDadosProfissionaisInput dadosProfissionais) {
        boolean atualizado = perfilService.atualizarDadosProfissionais(usuarioId, dadosProfissionais);
        if (!atualizado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/voluntario/disponibilidade")
    public ResponseEntity<Void> criarDisponibilidade(
            @RequestParam Integer usuarioId,
            @RequestBody Map<String, Object> disponibilidade) {
        boolean criado = perfilService.criarDisponibilidade(usuarioId, disponibilidade);
        if (!criado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(201).build();
    }

    @PatchMapping("/voluntario/disponibilidade")
    public ResponseEntity<Void> atualizarDisponibilidade(
            @RequestParam Integer usuarioId,
            @RequestBody Map<String, Object> disponibilidade) {
        boolean atualizado = perfilService.atualizarDisponibilidade(usuarioId, disponibilidade);
        if (!atualizado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/assistente-social")
    public ResponseEntity<AssistenteSocialOutput> buscarPerfilAssistenteSocial(@RequestParam Integer usuarioId) {
        try {
            AssistenteSocialOutput perfil = assistenteSocialService.buscarAssistenteSocial(usuarioId);
            if (perfil == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(perfil);
        } catch (Exception e) {
            LOGGER.error("Erro ao buscar perfil do assistente social: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}