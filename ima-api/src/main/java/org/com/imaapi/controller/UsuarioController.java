package org.com.imaapi.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.com.imaapi.model.usuario.Ficha;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.UsuarioMapper;
import org.com.imaapi.model.usuario.input.UsuarioAutenticacaoInput;
import org.com.imaapi.model.usuario.input.UsuarioInputPrimeiraFase;
import org.com.imaapi.model.usuario.input.UsuarioInputSegundaFase;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioPrimeiraFaseOutput;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.service.UsuarioService;
import org.com.imaapi.service.EnderecoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EnderecoService enderecoService;


    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioController.class);

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
        try {
            Usuario usuario = usuarioService.cadastrarSegundaFaseVoluntario(idUsuario, usuarioInputSegundaFase);
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioTokenOutput> login(@RequestBody @Valid UsuarioAutenticacaoInput usuarioAutenticacaoInput) {
        LOGGER.info("Requisição de login recebida para email: {}", usuarioAutenticacaoInput.getEmail());

        // Buscar o usuário pelo email
        Optional<Usuario> usuarioOpt = usuarioService.buscaUsuarioPorEmail(usuarioAutenticacaoInput.getEmail());

        if (usuarioOpt.isEmpty()) {
            LOGGER.warn("Usuário não encontrado para email: {}", usuarioAutenticacaoInput.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioOpt.get();

        // Validar senha (aqui pode-se usar passwordEncoder.matches)
        if (!passwordEncoder.matches(usuarioAutenticacaoInput.getSenha(), usuario.getSenha())) {
            LOGGER.warn("Senha inválida para email: {}", usuarioAutenticacaoInput.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Buscar a ficha associada
        Ficha ficha = usuario.getFicha();

        UsuarioTokenOutput usuarioTokenOutput = usuarioService.autenticar(usuario, ficha);

        if (usuarioTokenOutput == null) {
            LOGGER.warn("Falha na autenticação para email: {}", usuarioAutenticacaoInput.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        LOGGER.info("Login bem-sucedido para email: {}", usuarioAutenticacaoInput.getEmail());
        return ResponseEntity.ok(usuarioTokenOutput);
    }



    @GetMapping
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<List<UsuarioListarOutput>> listarUsuarios() {
        List<UsuarioListarOutput> usuarios = usuarioService.buscarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Usuario> buscarUsuario(@PathVariable Integer id) {
        return usuarioService.buscaUsuario(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/por-email")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Usuario> buscarUsuarioPorEmail(@RequestParam String email) {
        return usuarioService.buscaUsuarioPorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<UsuarioListarOutput> atualizarUsuario(
            @PathVariable Integer id,
            @RequestBody @Valid UsuarioInputSegundaFase usuarioInputSegundaFase) {
        UsuarioListarOutput usuarioAtualizado = usuarioService.atualizarUsuario(id, usuarioInputSegundaFase);
        return usuarioAtualizado != null ? 
               ResponseEntity.ok(usuarioAtualizado) : 
               ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Integer id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verificar-cadastro")
    public ResponseEntity<UsuarioPrimeiraFaseOutput> verificarCadastro(
            @RequestParam(required = false) Integer idUsuario,
            @RequestParam(required = false) String email) {
        try {
            Usuario usuario;
            if (idUsuario != null) {
                usuario = usuarioService.buscarDadosPrimeiraFase(idUsuario);
            } else if (email != null && !email.isEmpty()) {
                usuario = usuarioService.buscarDadosPrimeiraFase(email);
            } else {
                return ResponseEntity.badRequest().build();
            }
            
            UsuarioPrimeiraFaseOutput output = UsuarioMapper.ofPrimeiraFase(usuario);
            return ResponseEntity.ok(output);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/validar-cep/{cep}")
    public ResponseEntity<?> validarCep(@PathVariable String cep) {
        try {
            ResponseEntity<EnderecoOutput> response = enderecoService.buscaEndereco(cep, null, null);
            if (response.getBody() == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}