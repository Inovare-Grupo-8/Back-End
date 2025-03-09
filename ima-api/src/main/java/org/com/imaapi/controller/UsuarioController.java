package org.com.imaapi.controller;

import org.com.imaapi.model.Usuario;
import org.com.imaapi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.cadastrarUsuario(usuario);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarUsuarios() {
        return usuarioService.buscarUsuarios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Usuario>> buscaUsuario(@PathVariable Long id) {
        return usuarioService.buscaUsuario(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        return usuarioService.atualizarUsuario(id, usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        return usuarioService.deletarUsuario(id);
    }
}