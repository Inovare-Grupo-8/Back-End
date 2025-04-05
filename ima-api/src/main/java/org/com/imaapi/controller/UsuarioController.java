package org.com.imaapi.controller;

import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.input.UsuarioInput;
import org.com.imaapi.model.usuario.output.UsuarioOutput;
import org.com.imaapi.service.UsuarioService;
import org.com.imaapi.service.impl.UsuarioServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);


    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioOutput> cadastrarUsuario(@RequestBody UsuarioInput usuarioInput) {
        logger.info("Controller - cadastrarUsuario", usuarioInput);
        return usuarioService.cadastrarUsuario(usuarioInput);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarUsuarios() {
        return usuarioService.buscarUsuarios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Usuario>> buscaUsuario(@PathVariable Integer id) {
        return usuarioService.buscaUsuario(id);
    }

    @GetMapping("/por-nome")
    public ResponseEntity<List<Usuario>> buscaUsuarioPorNome(@RequestParam String nome) {
        return usuarioService.buscaUsuarioPorNome(nome);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Integer id, @RequestBody UsuarioInput usuarioInput) {
        return usuarioService.atualizarUsuario(id, usuarioInput);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Integer id) {
        return usuarioService.deletarUsuario(id);
    }
}