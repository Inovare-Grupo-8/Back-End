package org.com.imaapi.service.impl;

import org.com.imaapi.config.GerenciadorTokenJwt;
import org.com.imaapi.model.usuario.Endereco;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.UsuarioMapper;
import org.com.imaapi.model.usuario.input.UsuarioInput;
import org.com.imaapi.model.usuario.input.VoluntarioInput;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.repository.EnderecoRepository;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.EmailService;
import org.com.imaapi.service.EnderecoService;
import org.com.imaapi.service.UsuarioService;
import org.com.imaapi.service.VoluntarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VoluntarioService voluntarioService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private GerenciadorTokenJwt gerenciadorTokenJwt;
    @Autowired
    private EnderecoService enderecoService;
    @Autowired
    private EnderecoRepository enderecoRepository;

    public void cadastrarUsuario(UsuarioInput usuarioInput) {
        String senhaCriptografada = passwordEncoder.encode(usuarioInput.getSenha());
        usuarioInput.setSenha(senhaCriptografada);
        Usuario novoUsuario = UsuarioMapper.of(usuarioInput);


        logger.info("Cadastrando usuário: {}", usuarioInput);

        Endereco endereco = buscarOuSalvarEndereco(usuarioInput.getCep(), usuarioInput.getNumero());
        if (endereco == null) {
            logger.error("Endereço não encontrado para o CEP: {}", usuarioInput.getCep());
            throw new IllegalArgumentException("Endereço inválido para o CEP informado.");
        }

        novoUsuario.setEndereco(endereco);

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        logger.info("Usuário cadastrado com sucesso: {}", usuarioInput);

        if (usuarioInput.getIsVoluntario()) {
            VoluntarioInput voluntarioInput = UsuarioMapper.of(usuarioInput, usuarioSalvo.getIdUsuario());
            voluntarioService.cadastrarVoluntario(voluntarioInput);
            logger.info("Voluntário cadastrado com sucesso: {}", usuarioInput);
            emailService.enviarEmail(usuarioInput.getEmail(), usuarioInput.getNome(), "cadastro de voluntario");
        }else {
            emailService.enviarEmail(usuarioInput.getEmail(), usuarioInput.getNome(), "cadastro de email");
        }
    }

    private Endereco buscarOuSalvarEndereco(String cep, String numero) {
        ResponseEntity<EnderecoOutput> enderecoResponse = enderecoService.buscaEndereco(cep, numero);
        EnderecoOutput enderecoOutput = enderecoResponse.getBody();

        if (enderecoOutput == null) {
            return null;
        }

        Endereco endereco = new Endereco();
        endereco.setCep(enderecoOutput.getCep());
        endereco.setLogradouro(enderecoOutput.getLogradouro());
        endereco.setBairro(enderecoOutput.getBairro());
        endereco.setNumero(enderecoOutput.getNumero());
        endereco.setComplemento(enderecoOutput.getComplemento());
        endereco.setUf(enderecoOutput.getUf());
        endereco.setLocalidade(enderecoOutput.getLocalidade());
        return enderecoRepository.save(endereco);
    }

    public UsuarioTokenOutput autenticar(Usuario usuario) {
        final UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(), usuario.getSenha());

        final Authentication authentication = authenticationManager.authenticate(credentials);

        Usuario usuarioAutenticado = usuarioRepository.findByEmail(usuario.getEmail())
                .orElseThrow(
                    () -> new ResponseStatusException(404, "Email de usuário não cadastrado", null)
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String token = gerenciadorTokenJwt.generateToken(authentication);

        return UsuarioMapper.of(usuarioAutenticado, token);
    }

    public List<UsuarioListarOutput> buscarUsuarios() {
            logger.info("Buscando todos os usuários");
            List<Usuario> usuarios = usuarioRepository.findAll();

            logger.info("Usuários encontrados: {}", usuarios);
            return usuarios.stream().map(UsuarioMapper::of).toList();
    }

    public Optional<Usuario> buscaUsuario(Integer id) {
            logger.info("Buscando usuário com ID: {}", id);
            Optional<Usuario> usuario = usuarioRepository.findById(id);

            usuario.ifPresentOrElse(usuario1 -> {
                logger.info("Usuário encontrado: {}", usuario1);
            }, () -> {
                logger.warn("Usuário com ID {} não encontrado", id);
            });

            return usuario;
    }

    public Optional<Usuario> buscaUsuarioPorNome(String nome) {
            logger.info("Buscando usuário com nome: {}", nome);
            Optional<Usuario> usuario = usuarioRepository.findByNome(nome);

            usuario.ifPresentOrElse(usuario1 -> {
                logger.info("Usuário encontrado: {}", usuario1);
            }, () -> {
                logger.error("Erro ao buscar usuário: {}", nome);
            });

            return usuario;
    }

    public UsuarioListarOutput atualizarUsuario(Integer id, UsuarioInput usuarioInput) {
        logger.info("Atualizando usuário com ID: {}", id);

            try {
                Usuario usuario = UsuarioMapper.of(usuarioInput);
                usuario.setIdUsuario(id);
                usuarioRepository.save(usuario);
                UsuarioListarOutput usuarioListar = UsuarioMapper.of(usuario);
                logger.info("Usuário atualizado com sucesso: {}", usuarioListar);
                return usuarioListar;
            } catch (Exception erro) {
                logger.error("Erro ao atualizar usuário: {}", erro.getMessage());
                return null;
            }
    }

    public void deletarUsuario(Integer id) {
        logger.info("Deletando usuário com ID: {}", id);

        try {
            voluntarioService.excluirVoluntario(id);
            usuarioRepository.deleteById(id);
            logger.info("Usuário com ID {} deletado com sucesso", id);
        } catch (Exception erro) {
            logger.error("Erro ao deletar usuário: {}", erro.getMessage());
        }
    }
}