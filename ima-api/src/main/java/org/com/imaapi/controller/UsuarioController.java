package org.com.imaapi.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.UsuarioMapper;
import org.com.imaapi.model.usuario.input.UsuarioAutenticacaoInput;
import org.com.imaapi.model.usuario.input.UsuarioInput;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Void> cadastrarUsuario(@RequestBody @Valid UsuarioInput usuarioInput) {
            usuarioService.cadastrarUsuario(usuarioInput);
            return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/voluntario")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Void> cadastrarVoluntario(@RequestBody @Valid UsuarioInput usuarioInput) {
        usuarioService.cadastrarVoluntario(usuarioInput);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioTokenOutput> login(@RequestBody @Valid UsuarioAutenticacaoInput usuarioAutenticacaoInput) {
            Usuario usuario = UsuarioMapper.of(usuarioAutenticacaoInput);
            UsuarioTokenOutput usuarioTokenOutput = usuarioService.autenticar(usuario);

            if (usuarioTokenOutput == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            return new ResponseEntity<>(usuarioTokenOutput, HttpStatus.OK);
    }

    @GetMapping
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<List<UsuarioListarOutput>> buscarUsuarios() {
            List<UsuarioListarOutput> usuarios = usuarioService.buscarUsuarios();

            if (usuarios.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Optional<Usuario>> buscaUsuario(@PathVariable Integer id) {
            Optional<Usuario> usuario = usuarioService.buscaUsuario(id);

            if (usuario.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @GetMapping("/por-nome")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Optional<Usuario>> buscaUsuarioPorNome(@RequestParam String nome) {
            Optional<Usuario> usuario = usuarioService.buscaUsuarioPorNome(nome);
            return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<UsuarioListarOutput> atualizarUsuario(@PathVariable Integer id, @RequestBody UsuarioInput usuarioInput) {
        UsuarioListarOutput usuarioAtualizado = usuarioService.atualizarUsuario(id, usuarioInput);

        if (usuarioAtualizado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(usuarioAtualizado, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Integer id) {
        usuarioService.deletarUsuario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}