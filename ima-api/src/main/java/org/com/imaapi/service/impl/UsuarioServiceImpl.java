package org.com.imaapi.service.impl;

import org.com.imaapi.config.GerenciadorTokenJwt;
import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.usuario.Endereco;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.UsuarioMapper;
import org.com.imaapi.model.usuario.input.UsuarioInputPrimeiraFase;
import org.com.imaapi.model.usuario.input.UsuarioInputSegundaFase;
import org.com.imaapi.model.usuario.input.VoluntarioInput;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.EmailService;
import org.com.imaapi.service.UsuarioService;
import org.com.imaapi.service.VoluntarioService;
import org.com.imaapi.service.endereco.EnderecoHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Override
    public Usuario cadastrarPrimeiraFase(UsuarioInputPrimeiraFase usuarioInputPrimeiraFase) {
        String senhaCriptografada = passwordEncoder.encode(usuarioInputPrimeiraFase.getSenha());
        usuarioInputPrimeiraFase.setSenha(senhaCriptografada);
        
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(usuarioInputPrimeiraFase.getNome());
        novoUsuario.setEmail(usuarioInputPrimeiraFase.getEmail());
        novoUsuario.setSenha(senhaCriptografada);
        novoUsuario.setCpf(usuarioInputPrimeiraFase.getCpf());
        novoUsuario.setDataNascimento(usuarioInputPrimeiraFase.getDataNascimento());

        logger.info("Iniciando cadastro de usuário fase 1: {}", usuarioInputPrimeiraFase);
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        logger.info("Usuário fase 1 cadastrado com sucesso. ID: {}", usuarioSalvo.getIdUsuario());

        emailService.enviarEmail(usuarioSalvo.getEmail(), usuarioSalvo.getNome() + "|" + usuarioSalvo.getIdUsuario(), "continuar cadastro");

        return usuarioSalvo;
    }

    @Override
    public Usuario cadastrarSegundaFase(Integer idUsuario, UsuarioInputSegundaFase usuarioInputSegundaFase) {
        logger.info("Iniciando cadastro fase 2 para usuário ID: {}", idUsuario);
        
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        usuario.setGenero(usuarioInputSegundaFase.getGenero());
        usuario.setTipo(usuarioInputSegundaFase.getTipo());
        
        // Buscar e salvar endereço
        Endereco endereco = enderecoHandlerService.buscarSalvarEndereco(
                usuarioInputSegundaFase.getCep(),
                usuarioInputSegundaFase.getNumero(),
                usuarioInputSegundaFase.getComplemento());

        if (endereco == null) {
            logger.error("Endereço não encontrado para o CEP: {}", usuarioInputSegundaFase.getCep());
            throw new IllegalArgumentException("Endereço inválido para o CEP: " + usuarioInputSegundaFase.getCep());
        }

        usuario.setEndereco(endereco);

        usuarioRepository.save(usuario);
        logger.info("Usuário fase 2 atualizado com sucesso. ID: {}", idUsuario);
        
        emailService.enviarEmail(usuario.getEmail(), usuario.getNome(), "bem vindo");
        return usuario;
    }

    @Override
    public Usuario cadastrarSegundaFaseVoluntario(Integer idUsuario, UsuarioInputSegundaFase usuarioInputSegundaFase) {
        logger.info("Iniciando cadastro fase 2 para voluntário ID: {}", idUsuario);
        
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        usuario.setGenero(usuarioInputSegundaFase.getGenero());
        usuario.setTipo(TipoUsuario.VOLUNTARIO);
        
        usuarioRepository.save(usuario);
        
        VoluntarioInput voluntarioInput = UsuarioMapper.of(usuarioInputSegundaFase, idUsuario);
        voluntarioService.cadastrarVoluntario(voluntarioInput);
        
        logger.info("Voluntário fase 2 atualizado com sucesso. ID: {}", idUsuario);
        emailService.enviarEmail(usuario.getEmail(), usuario.getNome(), "bem vindo voluntario");
        return usuario;
    }

    @Override
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

    @Override
    public List<UsuarioListarOutput> buscarUsuarios() {
        logger.info("Buscando todos os usuários");
        List<Usuario> usuarios = usuarioRepository.findAll();

        logger.info("Usuários encontrados: {}", usuarios);
        return usuarios.stream().map(UsuarioMapper::of).toList();
    }

    @Override
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

    @Override
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

    @Override
    public UsuarioListarOutput atualizarUsuario(Integer id, UsuarioInputSegundaFase usuarioInputSegundaFase) {
        logger.info("Atualizando usuário com ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não cadastrado"));
        usuario.setGenero(usuarioInputSegundaFase.getGenero());
        usuario.setTipo(usuarioInputSegundaFase.getTipo());

        usuarioRepository.save(usuario);

        UsuarioListarOutput usuarioListar = UsuarioMapper.of(usuario);
        logger.info("Usuário atualizado: {}", usuarioListar);
        return usuarioListar;
    }

    @Override
    public void deletarUsuario(Integer id) {
        logger.info("Deletando usuário com ID: {}", id);

        voluntarioService.excluirVoluntario(id);
        usuarioRepository.deleteById(id);
        logger.info("Usuário com ID {} deletado", id);
    }

    @Override
    public Usuario buscarDadosPrimeiraFase(Integer idUsuario) {
        logger.info("Buscando dados da primeira fase do usuário ID: {}", idUsuario);
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Override
    public Usuario buscarDadosPrimeiraFase(String email) {
        logger.info("Buscando dados da primeira fase do usuário por email: {}", email);
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }


//        Endereco endereco = enderecoHandlerService.buscarSalvarEndereco(
//                usuarioInputSegundaFase.getCep(),
//                usuarioInputSegundaFase.getNumero(),
//                usuarioInputSegundaFase.getComplemento());
//
//        if (endereco == null) {
//            logger.error("Endereço não encontrado para o CEP: {}", usuarioInputSegundaFase.getCep());
//            throw new IllegalArgumentException("Endereço inválido para o CEP: " + usuarioInputSegundaFase.getCep());
//        }
//
//        novoUsuario.setEndereco(endereco);
}