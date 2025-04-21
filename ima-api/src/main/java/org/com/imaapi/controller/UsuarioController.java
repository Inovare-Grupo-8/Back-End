package org.com.imaapi.controller;

import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.UsuarioMapper;
import org.com.imaapi.model.usuario.Voluntario;
import org.com.imaapi.model.usuario.input.UsuarioInput;
import org.com.imaapi.model.usuario.input.VoluntarioInput;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioOutput;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody UsuarioInput usuarioInput) {
        try {
            usuarioService.cadastrarUsuario(usuarioInput);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception erro) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioListarOutput>> buscarUsuarios() {
        try {
            List<UsuarioListarOutput> usuarios = usuarioService.buscarUsuarios();
            return new ResponseEntity<>(usuarios, HttpStatus.OK);
        } catch (Exception erro) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Usuario>> buscaUsuario(@PathVariable Integer id) {
        try {
            Optional<Usuario> usuario = usuarioService.buscaUsuario(id);
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (Exception erro) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/por-nome")
    public ResponseEntity<Optional<Usuario>> buscaUsuarioPorNome(@RequestParam String nome) {
        try {
            Optional<Usuario> usuario = usuarioService.buscaUsuarioPorNome(nome);
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (Exception erro) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioListarOutput> atualizarUsuario(@PathVariable Integer id, @RequestBody UsuarioInput usuarioInput) {

        if (usuarioService.buscaUsuario(id).isPresent()) {
            try {
                UsuarioListarOutput usuarioAtualizado = usuarioService.atualizarUsuario(id, usuarioInput);
                return new ResponseEntity<>(usuarioAtualizado, HttpStatus.ACCEPTED);
            }
            catch (Exception erro) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Integer id) {

        if (usuarioService.buscaUsuario(id).isPresent()) {
            try {
                usuarioService.deletarUsuario(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (Exception erro) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}