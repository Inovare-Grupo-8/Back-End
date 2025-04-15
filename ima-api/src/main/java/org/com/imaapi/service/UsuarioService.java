package org.com.imaapi.service;

import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.input.UsuarioInput;
import org.com.imaapi.model.usuario.output.UsuarioOutput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    public ResponseEntity<UsuarioOutput> cadastrarUsuario(@RequestBody UsuarioInput usuarioInput);
    public ResponseEntity<List<Usuario>> buscarUsuarios();
    public ResponseEntity<Optional<Usuario>> buscaUsuario(@PathVariable Integer id);
    public ResponseEntity<List<Usuario>> buscaUsuarioPorNome(@RequestParam String nome);
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Integer id, UsuarioInput usuarioInput);
    public ResponseEntity<Void> deletarUsuario(@PathVariable Integer id);
}