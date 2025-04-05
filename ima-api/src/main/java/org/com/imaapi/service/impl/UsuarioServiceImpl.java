package org.com.imaapi.service.impl;

import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.Voluntario;
import org.com.imaapi.model.usuario.input.UsuarioInput;
import org.com.imaapi.model.usuario.input.VoluntarioInput;
import org.com.imaapi.model.usuario.output.UsuarioOutput;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.repository.VoluntarioRepository;
import org.com.imaapi.service.EmailService;
import org.com.imaapi.service.UsuarioService;
import org.com.imaapi.service.VoluntarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VoluntarioService voluntarioService;

    @Autowired
    private EmailService emailService;

    public ResponseEntity<UsuarioOutput> cadastrarUsuario(@RequestBody UsuarioInput usuarioInput) {
        UsuarioOutput usuarioResponse;
        try {
            logger.info("Cadastrando usuário: {}", usuarioInput);
            Usuario usuarioSalvo = gerarObjetoUsuario(usuarioInput);
            Usuario salvarUsuario = usuarioRepository.save(usuarioSalvo);
            logger.info("Usuário cadastrado com sucesso: {}", salvarUsuario);

            usuarioResponse = gerarObjetoUsuarioOutput(salvarUsuario);

            if (Boolean.TRUE.equals(usuarioInput.getIsVoluntario())) {
                VoluntarioInput voluntarioInput = gerarObjetoVoluntario(usuarioInput, salvarUsuario.getIdUsuario());
                Voluntario voluntario = voluntarioService.cadastrarVoluntario(voluntarioInput).getBody();
                logger.info("Voluntário cadastrado com sucesso: {}", voluntario);
                usuarioResponse.setFuncao(voluntario.getFuncao());
            }

            emailService.enviarEmail(usuarioInput.getEmail(), usuarioInput.getNome(), "");

            return new ResponseEntity<>(usuarioResponse, HttpStatus.CREATED);
        } catch (Exception erro) {
            logger.error("Erro ao cadastrar usuário: {}", erro.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Usuario>> buscarUsuarios() {
        try {
            logger.info("Buscando todos os usuários");
            List<Usuario> usuarios = usuarioRepository.findAll();
            logger.info("Usuários encontrados: {}", usuarios);
            return new ResponseEntity<>(usuarios, HttpStatus.OK);
        } catch (Exception erro) {
            logger.error("Erro ao buscar usuários: {}", erro.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Optional<Usuario>> buscaUsuario(@PathVariable Integer id) {
        try {
            logger.info("Buscando usuário com ID: {}", id);
            Optional<Usuario> usuario = usuarioRepository.findById(id);
            if (usuario.isPresent()) {
                logger.info("Usuário encontrado: {}", usuario.get());
                return new ResponseEntity<>(usuario, HttpStatus.OK);
            } else {
                logger.warn("Usuário com ID {} não encontrado", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception erro) {
            logger.error("Erro ao buscar usuário: {}", erro.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Usuario>> buscaUsuarioPorNome(@RequestParam String nome) {
        try {
            logger.info("Buscando usuários com nome: {}", nome);
            List<Usuario> usuario = usuarioRepository.findByNome(nome);
                return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (Exception erro) {
            logger.error("Erro ao buscar usuário: {}", erro.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Usuario> atualizarUsuario(Integer id, UsuarioInput usuarioInput) {
        try {
            logger.info("Atualizando usuário com ID: {}", id);
            if (usuarioRepository.existsById(id)) {
                Usuario usuario = gerarObjetoUsuario(usuarioInput);
                usuario.setIdUsuario(id);
                Usuario atualizado = usuarioRepository.save(usuario);
                logger.info("Usuário atualizado com sucesso: {}", atualizado);
                return new ResponseEntity<>(atualizado, HttpStatus.OK);
            }
            logger.warn("Usuário com ID {} não encontrado para atualização", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception erro) {
            logger.error("Erro ao atualizar usuário: {}", erro.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Void> deletarUsuario(Integer id) {
        try {
            logger.info("Deletando usuário com ID: {}", id);
            if (usuarioRepository.existsById(id)) {
                voluntarioService.excluirVoluntario(id);
                usuarioRepository.deleteById(id);
                logger.info("Usuário com ID {} deletado com sucesso", id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            logger.warn("Usuário com ID {} não encontrado para deleção", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception erro) {
            logger.error("Erro ao deletar usuário: {}", erro.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private UsuarioOutput gerarObjetoUsuarioOutput(Usuario usuario) {
        UsuarioOutput usuarioResponse = new UsuarioOutput();
        usuarioResponse.setId(usuario.getIdUsuario());
        usuarioResponse.setNome(usuario.getNome());
        usuarioResponse.setEmail(usuario.getEmail());
        usuarioResponse.setSenha(usuario.getSenha());
        usuarioResponse.setCpf(usuario.getCpf());
        usuarioResponse.setDataNascimento(usuario.getDataNascimento());
        usuarioResponse.setRenda(usuario.getRenda());
        usuarioResponse.setDataCadastro(usuario.getDataCadastro());
        usuarioResponse.setGenero(usuario.getGenero());
        return usuarioResponse;
    }

    private Usuario gerarObjetoUsuario(UsuarioInput usuarioInput) {
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioInput.getNome());
        usuario.setEmail(usuarioInput.getEmail());
        usuario.setSenha(usuarioInput.getSenha());
        usuario.setCpf(usuarioInput.getCpf());
        usuario.setDataNascimento(usuarioInput.getDataNascimento());
        usuario.setRenda(usuarioInput.getRenda());
        usuario.setGenero(usuarioInput.getGenero());
        usuario.setDataCadastro(LocalDateTime.now());
        return usuario;
    }

    private VoluntarioInput gerarObjetoVoluntario(UsuarioInput usuarioInput, Integer idUsuario) {
        VoluntarioInput voluntario = new VoluntarioInput();
        voluntario.setFkUsuario(idUsuario);
        voluntario.setFuncao(usuarioInput.getFuncao());
        return voluntario;
    }
}