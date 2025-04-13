package org.com.imaapi.controller;

import jakarta.validation.Valid;
import org.com.imaapi.model.usuario.input.AutenticacaoInput;
import org.com.imaapi.model.usuario.input.UsuarioInput;
import org.com.imaapi.model.usuario.output.UsuarioOutput;
import org.com.imaapi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("auth")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<UserDetails> autenticar(@RequestBody @Valid AutenticacaoInput usuario) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(usuario.getEmail(), usuario.getSenha());
        var auth = authenticationManager.authenticate(token);

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioOutput> registrarUsuario(@RequestBody @Valid UsuarioInput usuario) {
        return usuarioService.cadastrarUsuario(usuario);
    }
}
