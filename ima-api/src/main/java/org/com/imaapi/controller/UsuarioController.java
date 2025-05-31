package org.com.imaapi.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.UsuarioMapper;
import org.com.imaapi.model.usuario.input.UsuarioAutenticacaoInput;
import org.com.imaapi.model.usuario.input.UsuarioInputPrimeiraFase;
import org.com.imaapi.model.usuario.input.UsuarioInputSegundaFase;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioPrimeiraFaseOutput;
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

    @PostMapping("/fase1")
    public ResponseEntity<Usuario> cadastrarUsuarioFase1(@RequestBody @Valid UsuarioInputPrimeiraFase usuarioInputPrimeiraFase) {
        Usuario usuario = usuarioService.cadastrarPrimeiraFase(usuarioInputPrimeiraFase);
        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }

    @PatchMapping("/fase2/{idUsuario}")
    public ResponseEntity<Usuario> completarCadastroUsuario(@PathVariable Integer idUsuario, @RequestBody @Valid UsuarioInputSegundaFase usuarioInputSegundaFase) {
        Usuario usuario = usuarioService.cadastrarSegundaFase(idUsuario, usuarioInputSegundaFase);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/voluntario/fase1")
    public ResponseEntity<Usuario> cadastrarVoluntarioFase1(@RequestBody @Valid UsuarioInputPrimeiraFase usuarioInputPrimeiraFase) {
        Usuario usuario = usuarioService.cadastrarPrimeiraFase(usuarioInputPrimeiraFase);
        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }

    @PatchMapping("/voluntario/fase2/{idUsuario}")
    public ResponseEntity<Usuario> completarCadastroVoluntario(
            @PathVariable Integer idUsuario,
            @RequestBody @Valid UsuarioInputSegundaFase usuarioInputSegundaFase) {
        Usuario usuario = usuarioService.cadastrarSegundaFaseVoluntario(idUsuario, usuarioInputSegundaFase);
        return ResponseEntity.ok(usuario);
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
    public ResponseEntity<UsuarioListarOutput> atualizarUsuario(@PathVariable Integer id, @RequestBody UsuarioInputSegundaFase usuarioInputSegundaFase) {
        UsuarioListarOutput usuarioAtualizado = usuarioService.atualizarUsuario(id, usuarioInputSegundaFase);

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
    }    @GetMapping("/fase1/{idUsuario}")
    public ResponseEntity<UsuarioPrimeiraFaseOutput> buscarDadosPrimeiraFase(@PathVariable Integer idUsuario) {
        Usuario usuario = usuarioService.buscarDadosPrimeiraFase(idUsuario);
        UsuarioPrimeiraFaseOutput output = UsuarioMapper.ofPrimeiraFase(usuario);
        return ResponseEntity.ok(output);
    }

    @GetMapping("/voluntario/fase1/{idUsuario}")
    public ResponseEntity<UsuarioPrimeiraFaseOutput> buscarDadosPrimeiraFaseVoluntario(@PathVariable Integer idUsuario) {
        Usuario usuario = usuarioService.buscarDadosPrimeiraFase(idUsuario);
        UsuarioPrimeiraFaseOutput output = UsuarioMapper.ofPrimeiraFase(usuario);
        return ResponseEntity.ok(output);
    }
}