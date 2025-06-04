package org.com.imaapi.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.com.imaapi.model.usuario.Ficha;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.UsuarioMapper;
import org.com.imaapi.model.usuario.input.UsuarioAutenticacaoInput;
import org.com.imaapi.model.usuario.input.UsuarioInputPrimeiraFase;
import org.com.imaapi.model.usuario.input.UsuarioInputSegundaFase;
import org.com.imaapi.model.usuario.input.VoluntarioInputSegundaFase;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioPrimeiraFaseOutput;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.service.EnderecoService;
import org.com.imaapi.service.UsuarioService;
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
    public ResponseEntity<Usuario> cadastrarUsuarioFase1(

            @RequestBody @Valid UsuarioInputPrimeiraFase usuarioInputPrimeiraFase) {
        LOGGER.info("Iniciando cadastro primeira fase. Dados: {}", usuarioInputPrimeiraFase);

        try {
            Usuario usuario = usuarioService.cadastrarPrimeiraFase(usuarioInputPrimeiraFase);
            LOGGER.info("Cadastro primeira fase concluído com sucesso. ID={}", usuario.getIdUsuario());
            return new ResponseEntity<>(usuario, HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.error("Erro ao cadastrar usuário na primeira fase: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping({"/fase2/{idUsuario}", "/assistido/fase2/{idUsuario}"})
    public ResponseEntity<Usuario> completarCadastroAssistido(
            @PathVariable Integer idUsuario,
            @RequestBody @Valid UsuarioInputSegundaFase usuarioInputSegundaFase) {
        try {
            LOGGER.info("Iniciando cadastro fase 2 para assistido ID: {}", idUsuario);

            Usuario usuario = usuarioService.cadastrarSegundaFase(idUsuario, usuarioInputSegundaFase);
            LOGGER.info("Cadastro fase 2 concluído com sucesso: ID={}", idUsuario);
            return ResponseEntity.ok(usuario);
        } catch (IllegalStateException e) {
            LOGGER.info("Tentativa de usar endpoint errado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header("X-Error-Message", "Use o endpoint /voluntario/fase2 para voluntários")
                    .build();
        } catch (IllegalArgumentException e) {
            LOGGER.error("Erro nos dados fornecidos para cadastro: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (UsernameNotFoundException e) {
            LOGGER.error("Usuário não encontrado para ID: {}", idUsuario);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping({"/voluntario/fase1", "/fase1/voluntario"})
    public ResponseEntity<Usuario> cadastrarVoluntarioFase1(@RequestBody @Valid UsuarioInputPrimeiraFase usuarioInputPrimeiraFase) {
        try {
            if (!Boolean.TRUE.equals(usuarioInputPrimeiraFase.getIsVoluntario())) {
                usuarioInputPrimeiraFase.setIsVoluntario(true); // Ensure it's set as volunteer
            }
            Usuario usuario = usuarioService.cadastrarPrimeiraFase(usuarioInputPrimeiraFase);
            LOGGER.info("Cadastro primeira fase de voluntário concluído com sucesso. ID={}", usuario.getIdUsuario());
            return new ResponseEntity<>(usuario, HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.error("Erro ao cadastrar voluntário na primeira fase: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/voluntario/fase2/{idUsuario}")
    public ResponseEntity<Usuario> completarCadastroVoluntario(
            @PathVariable Integer idUsuario,
            @RequestBody @Valid VoluntarioInputSegundaFase voluntarioInputSegundaFase) {
        try {
            LOGGER.info("Iniciando cadastro fase 2 para voluntário ID: {}", idUsuario);

            if (voluntarioInputSegundaFase.getFuncao() == null) {
                LOGGER.error("Função não informada para o voluntário ID: {}", idUsuario);
                return ResponseEntity.badRequest().build();
            }

            Usuario usuario = usuarioService.cadastrarSegundaFaseVoluntario(idUsuario, voluntarioInputSegundaFase);
            LOGGER.info("Cadastro fase 2 do voluntário concluído com sucesso: ID={}", idUsuario);
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Erro nos dados fornecidos para cadastro do voluntário: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (UsernameNotFoundException e) {
            LOGGER.error("Usuário não encontrado para ID: {}", idUsuario);
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