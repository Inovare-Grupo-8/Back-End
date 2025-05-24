/*package org.com.imaapi.service.impl;

import org.com.imaapi.config.GerenciadorTokenJwt;
import org.com.imaapi.model.usuario.Endereco;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.UsuarioMapper;
import org.com.imaapi.model.usuario.input.UsuarioInput;
import org.com.imaapi.model.usuario.input.VoluntarioInput;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.repository.EnderecoRepository;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.EmailService;
import org.com.imaapi.service.EnderecoService;
import org.com.imaapi.service.UsuarioService;
import org.com.imaapi.service.VoluntarioService;
import org.com.imaapi.service.endereco.EnderecoHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private EnderecoHandlerService enderecoHandlerService;

    public void cadastrarUsuario(UsuarioInput usuarioInput) {
        String senhaCriptografada = passwordEncoder.encode(usuarioInput.getSenha());
        usuarioInput.setSenha(senhaCriptografada);
        Usuario novoUsuario = UsuarioMapper.of(usuarioInput);

        logger.info("Cadastrando usuário: {}", usuarioInput);

        Endereco endereco = enderecoHandlerService.buscarSalvarEndereco(
                usuarioInput.getCep(),
                usuarioInput.getNumero(),
                usuarioInput.getComplemento());

        if (endereco == null) {
            logger.error("Endereço não encontrado para o CEP: {}", usuarioInput.getCep());
            throw new IllegalArgumentException("Endereço inválido para o CEP: " + usuarioInput.getCep());
        }

        novoUsuario.setEndereco(endereco);

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        logger.info("Usuário cadastrado com sucesso: {}", usuarioInput);

        emailService.enviarEmail(usuarioInput.getEmail(), usuarioInput.getNome(), "cadastro de email");

    }

    public void cadastrarVoluntario(UsuarioInput usuarioInput) {
        String senhaCriptografada = passwordEncoder.encode(usuarioInput.getSenha());
        usuarioInput.setSenha(senhaCriptografada);
        Usuario novoUsuario = UsuarioMapper.of(usuarioInput);

        logger.info("Cadastrando voluntario: {}", usuarioInput);

        Endereco endereco = enderecoHandlerService.buscarSalvarEndereco(
                usuarioInput.getCep(),
                usuarioInput.getNumero(),
                usuarioInput.getComplemento());

        if (endereco == null) {
            logger.error("Endereço não encontrado para o CEP: {}", usuarioInput.getCep());
            throw new IllegalArgumentException("Endereço inválido para o CEP: " + usuarioInput.getCep());
        }

        novoUsuario.setEndereco(endereco);

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        logger.info("Usuário cadastrado com sucesso: {}", usuarioInput);

        VoluntarioInput voluntarioInput = UsuarioMapper.of(usuarioInput, usuarioSalvo.getIdUsuario());
        voluntarioService.cadastrarVoluntario(voluntarioInput);
        logger.info("Voluntário cadastrado com sucesso: {}", usuarioInput);
        emailService.enviarEmail(usuarioInput.getEmail(), usuarioInput.getNome(), "cadastro de voluntario");

    }

    public UsuarioTokenOutput autenticar(Usuario usuario) {
        final UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(), usuario.getSenha());

        final Authentication authentication = authenticationManager.authenticate(credentials);

        Usuario usuarioAutenticado = usuarioRepository.findByEmail(usuario.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não cadastrado"));

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
            throw new UsernameNotFoundException(nome);
        });

        return usuario;
    }

    public UsuarioListarOutput atualizarUsuario(Integer id, UsuarioInput usuarioInput) {
        logger.info("Atualizando usuário com ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não cadastrado"));

        usuario.setEmail(usuarioInput.getEmail());
        usuario.setNome(usuarioInput.getNome());
        usuario.setSenha(usuarioInput.getSenha());
        usuario.setCpf(usuarioInput.getCpf());
        usuario.setDataNascimento(usuarioInput.getDataNascimento());
        usuario.setGenero(usuarioInput.getGenero());
        usuario.setRenda(usuarioInput.getRenda());
        usuario.setTipo(usuarioInput.getTipo());

        usuarioRepository.save(usuario);

        UsuarioListarOutput usuarioListar = UsuarioMapper.of(usuario);
        logger.info("Usuário atualizado: {}", usuarioListar);
        return usuarioListar;
    }

    public void deletarUsuario(Integer id) {
        logger.info("Deletando usuário com ID: {}", id);

        voluntarioService.excluirVoluntario(id);
        usuarioRepository.deleteById(id);
        logger.info("Usuário com ID {} deletado", id);
    }
}*/