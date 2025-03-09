package org.com.imaapi.service;

import org.com.imaapi.model.Usuario;
import org.com.imaapi.model.Cargo;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.repository.CargoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CargoRepository cargoRepository;

    public ResponseEntity<Usuario> cadastrarUsuario(Usuario usuario) {
        try {
            logger.info("Cadastrando usuário: {}", usuario);
            Cargo cargo = usuario.getCargo();
            if (cargo != null && cargo.getIdCargo() == null) {
                logger.info("Salvando cargo: {}", cargo);
                cargo = cargoRepository.save(cargo);
                usuario.setCargo(cargo);
            }
            Usuario salvarUsuario = usuarioRepository.save(usuario);
            logger.info("Usuário cadastrado com sucesso: {}", salvarUsuario);
            return new ResponseEntity<>(salvarUsuario, HttpStatus.CREATED);
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

    public ResponseEntity<Optional<Usuario>> buscaUsuario(Long id) {
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

    public ResponseEntity<Usuario> atualizarUsuario(Long id, Usuario usuario) {
        try {
            logger.info("Atualizando usuário com ID: {}", id);
            if (usuarioRepository.existsById(id)) {
                usuario.setId(id);
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

    public ResponseEntity<Void> deletarUsuario(Long id) {
        try {
            logger.info("Deletando usuário com ID: {}", id);
            if (usuarioRepository.existsById(id)) {
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
}