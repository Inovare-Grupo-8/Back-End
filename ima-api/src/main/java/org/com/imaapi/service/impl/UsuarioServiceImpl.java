package org.com.imaapi.service.impl;

import org.com.imaapi.config.GerenciadorTokenJwt;
import org.com.imaapi.model.enums.Genero;
import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.usuario.*;
import org.com.imaapi.model.usuario.input.UsuarioInputPrimeiraFase;
import org.com.imaapi.model.usuario.input.UsuarioInputSegundaFase;
import org.com.imaapi.model.usuario.input.VoluntarioInput;
import org.com.imaapi.model.usuario.input.VoluntarioInputSegundaFase;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.repository.EnderecoRepository;
import org.com.imaapi.repository.FichaRepository;
import org.com.imaapi.repository.TelefoneRepository;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.EmailService;
import org.com.imaapi.service.EnderecoService;
import org.com.imaapi.service.UsuarioService;
import org.com.imaapi.service.VoluntarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
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
    private EnderecoRepository enderecoRepository;

    @Autowired
    private TelefoneRepository telefoneRepository;

    @Autowired
    private FichaRepository fichaRepository;

    @Autowired
    private EnderecoService enderecoService;

    @Override
    public Usuario cadastrarPrimeiraFase(UsuarioInputPrimeiraFase usuarioInputPrimeiraFase) {
        logger.info("Iniciando cadastro da primeira fase do usuário. Dados recebidos: {}", usuarioInputPrimeiraFase);

        String senhaCriptografada = passwordEncoder.encode(usuarioInputPrimeiraFase.getSenha());
        logger.debug("Senha criptografada para o email {}: {}", usuarioInputPrimeiraFase.getEmail(), senhaCriptografada);
        
        Ficha ficha = new Ficha();
        ficha.setNome(usuarioInputPrimeiraFase.getNome());
        ficha.setSobrenome(usuarioInputPrimeiraFase.getSobrenome());
        logger.info("Ficha criada com nome: {}", ficha);
        
        Ficha fichaSalva = fichaRepository.save(ficha);
        logger.info("Ficha salva com ID: {}", fichaSalva.getIdFicha());

        Usuario novoUsuario = Usuario.criarUsuarioBasico(
                usuarioInputPrimeiraFase.getEmail(),
                senhaCriptografada,
                fichaSalva);

        // Se for voluntário, já define o tipo
        if (Boolean.TRUE.equals(usuarioInputPrimeiraFase.getIsVoluntario())) {
            novoUsuario.setTipo(TipoUsuario.VOLUNTARIO);
            logger.info("Usuário definido como VOLUNTARIO");
        }

        try {
            Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
            logger.info("Usuário fase 1 salvo no banco com ID: {} e email: {}", usuarioSalvo.getIdUsuario(), usuarioSalvo.getEmail());

            // Envia email apropriado baseado no tipo de usuário
            if (Boolean.TRUE.equals(usuarioInputPrimeiraFase.getIsVoluntario())) {
                emailService.enviarEmail(usuarioSalvo.getEmail(), usuarioSalvo.getFicha().getNome(), "bem vindo voluntario");
            } else {
                emailService.enviarEmail(usuarioSalvo.getEmail(),
                        usuarioSalvo.getFicha().getNome() + "|" + usuarioSalvo.getIdUsuario(),
                        "continuar cadastro");
            }
            logger.info("Email enviado para o usuário {} para continuar cadastro", usuarioSalvo.getEmail());

            return usuarioSalvo;
        } catch (Exception ex) {
            logger.error("Erro ao cadastrar usuário na primeira fase: {}", ex.getMessage(), ex);
            throw new RuntimeException("Erro ao cadastrar usuário na primeira fase", ex);
        }
    }

    @Override
    public void cadastrarUsuarioOAuth(OAuth2User usuario) {
        String nome = usuario.getAttribute("nome");
        String email = usuario.getAttribute("email");

        logger.info("Iniciando cadastro OAuth para usuário com email: {} e nome: {}", email, nome);

        Ficha ficha = new Ficha();
        ficha.setNome(nome);
        logger.info("Ficha criada para OAuth com nome: {}", ficha.getNome());

        Usuario novoUsuario = Usuario.criarUsuarioBasico(email, "", ficha);
        logger.info("Usuário OAuth criado com email: {} e ficha associada", novoUsuario.getEmail());

        usuarioRepository.save(novoUsuario);
        logger.info("Usuário OAuth salvo no banco com email: {}", novoUsuario.getEmail());
    }    
    
    @Override
    public Usuario cadastrarSegundaFase(Integer idUsuario, UsuarioInputSegundaFase usuarioInputSegundaFase) {
        logger.info("Iniciando cadastro da segunda fase para usuário ID: {}", idUsuario);
        logger.debug("Dados recebidos para atualização: {}", usuarioInputSegundaFase);

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para ID: " + idUsuario));
        logger.info("Usuário encontrado no banco: email={}, tipo={}", usuario.getEmail(), usuario.getTipo());

        // Se for voluntário, usar o método específico
        if (usuario.getTipo() == TipoUsuario.VOLUNTARIO) {
            logger.info("Usuário é voluntário, redirecionando para cadastro específico");
            throw new IllegalStateException("Para voluntários, use o endpoint específico de cadastro de voluntários");
        }

        Ficha ficha = usuario.getFicha();
        logger.info("Dados atuais da ficha antes da atualização: {}", ficha);

        if (usuarioInputSegundaFase.getEndereco() != null) {
            String cep = usuarioInputSegundaFase.getEndereco().getCep().replace("-", "");
            String numero = usuarioInputSegundaFase.getEndereco().getNumero();
            String complemento = usuarioInputSegundaFase.getEndereco().getComplemento();
            
            logger.info("Processando endereço no início do cadastro: CEP={}, numero={}", cep, numero);
            
            ResponseEntity<EnderecoOutput> enderecoResponse = enderecoService.buscaEndereco(cep, numero, complemento);
            EnderecoOutput enderecoOutput = enderecoResponse.getBody();
            
            if (enderecoOutput != null && enderecoOutput.getCep() != null) {
                Optional<Endereco> endereco = enderecoRepository.findByCepAndNumero(cep, numero);
                
                if (endereco.isPresent()) {
                    ficha.setEndereco(endereco.get());
                    logger.info("Endereço vinculado à ficha através da FK: endereco_id={}", endereco.get().getIdEndereco());
                } else {
                    logger.error("Endereço não encontrado após tentativa de cadastro: CEP={}, numero={}", cep, numero);
                    throw new RuntimeException("Erro ao processar endereço para vinculação com a ficha");
                }
            } else {
                logger.warn("Dados de endereço inválidos recebidos da API de CEP");
                throw new IllegalArgumentException("Dados de endereço inválidos");
            }
        } else {
            logger.warn("Nenhum endereço fornecido para o cadastro da segunda fase");
        }

        ficha.atualizarDadosSegundaFase(usuarioInputSegundaFase);
        logger.info("Ficha atualizada com os novos dados: {}", ficha);

        usuario.atualizarTipo(usuarioInputSegundaFase.getTipo());
        logger.info("Tipo do usuário atualizado para: {}", usuario.getTipo());
        usuarioRepository.save(usuario);
        logger.info("Usuário salvo após atualização da segunda fase: ID={}, email={}", usuario.getIdUsuario(), usuario.getEmail());

        if (usuarioInputSegundaFase.getTelefone() != null) {
            Telefone telefone = Telefone.of(usuarioInputSegundaFase.getTelefone(), ficha);
            telefoneRepository.save(telefone);
            logger.info("Telefone salvo para a ficha ID {}: {}", ficha.getIdFicha(), telefone);
        } else {
            logger.info("Nenhum telefone fornecido para atualização.");
        }

        emailService.enviarEmail(usuario.getEmail(), usuario.getFicha().getNome(), "bem vindo");
        logger.info("Email de boas-vindas enviado para o usuário: {}", usuario.getEmail());

        return usuario;
    }   
      @Override
    public Usuario cadastrarSegundaFaseVoluntario(Integer idUsuario, VoluntarioInputSegundaFase voluntarioInput) {
        logger.info("Iniciando cadastro fase 2 para voluntário ID: {}", idUsuario);
        
        if (voluntarioInput.getFuncao() == null) {
            logger.error("Função do voluntário não informada para ID: {}", idUsuario);
            throw new IllegalArgumentException("A função do voluntário deve ser informada");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para ID: " + idUsuario));

        if (usuario.getTipo() != TipoUsuario.VOLUNTARIO) {
            logger.error("Usuário ID {} não é um voluntário", idUsuario);
            throw new IllegalArgumentException("Apenas voluntários podem completar este cadastro");
        }

        Ficha ficha = usuario.getFicha();
        logger.info("Processando cadastro da segunda fase para o voluntário");
        
        // Atualizar dados básicos
        ficha.setCpf(voluntarioInput.getCpf());
        ficha.setDtNascim(voluntarioInput.getDataNascimento());
        ficha.setGenero(Genero.fromString(voluntarioInput.getGenero()));
        ficha.setRenda(voluntarioInput.getRenda() != null ? BigDecimal.valueOf(voluntarioInput.getRenda()) : null);
        ficha.setProfissao(voluntarioInput.getProfissao());
        
        // Processar endereço
        if (voluntarioInput.getEndereco() != null) {
            String cep = voluntarioInput.getEndereco().getCep().replace("-", "");
            String numero = voluntarioInput.getEndereco().getNumero();
            String complemento = voluntarioInput.getEndereco().getComplemento();
            
            logger.info("Processando endereço do voluntário: CEP={}, numero={}", cep, numero);
            
            ResponseEntity<EnderecoOutput> enderecoResponse = enderecoService.buscaEndereco(cep, numero, complemento);
            EnderecoOutput enderecoOutput = enderecoResponse.getBody();
            
            if (enderecoOutput != null && enderecoOutput.getCep() != null) {
                Optional<Endereco> endereco = enderecoRepository.findByCepAndNumero(cep, numero);
                
                if (endereco.isPresent()) {
                    ficha.setEndereco(endereco.get());
                    logger.info("Endereço vinculado à ficha do voluntário: endereco_id={}", endereco.get().getIdEndereco());
                } else {
                    logger.error("Endereço não encontrado após tentativa de cadastro: CEP={}, numero={}", cep, numero);
                    throw new RuntimeException("Erro ao processar endereço para vinculação com a ficha");
                }
            } else {
                logger.warn("Dados de endereço inválidos recebidos da API de CEP");
                throw new IllegalArgumentException("Dados de endereço inválidos");
            }
        }
        
        // Salvar telefone
        if (voluntarioInput.getTelefone() != null) {
            Telefone telefone = Telefone.of(voluntarioInput.getTelefone(), ficha);
            telefoneRepository.save(telefone);
            logger.info("Telefone salvo para o voluntário: {}", telefone);
        }
        
        // Garantir que o tipo seja VOLUNTARIO
        usuario.setTipo(TipoUsuario.VOLUNTARIO);
        
        // Salvar o usuário primeiro
        usuarioRepository.save(usuario);
        logger.info("Usuário voluntário salvo no banco: ID={}, email={}", usuario.getIdUsuario(), usuario.getEmail());
        
        // Criar registro de voluntário com a função
        VoluntarioInput volInput = new VoluntarioInput();
        volInput.setFkUsuario(usuario.getIdUsuario());
        volInput.setFuncao(voluntarioInput.getFuncao());
        
        // Salvar o registro do voluntário
        voluntarioService.cadastrarVoluntario(volInput);
        logger.info("Registro de voluntário criado com função: {}", voluntarioInput.getFuncao());
        
        emailService.enviarEmail(usuario.getEmail(), usuario.getFicha().getNome(), "bem vindo voluntario");
        logger.info("Email de boas-vindas enviado para o voluntário: {}", usuario.getEmail());

        // Recarregar o usuário com os dados atualizados
        return usuarioRepository.findById(usuario.getIdUsuario())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado após salvar"));
    }

    @Override
    public UsuarioTokenOutput autenticar(Usuario usuario, Ficha ficha) {
        logger.info("[AUTENTICAR] Iniciando autenticação para email: {}", usuario.getEmail());

        final UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(), usuario.getSenha(), UsuarioMapper.ofDetalhes(usuario, ficha).getAuthorities());

        final Authentication authentication = authenticationManager.authenticate(credentials);
        logger.info("[AUTENTICAR] AuthenticationManager autenticou as credenciais para: {}", usuario.getEmail());

        Usuario usuarioAutenticado = usuarioRepository.findByEmail(usuario.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não cadastrado: " + usuario.getEmail()));

        logger.info("[AUTENTICAR] Usuário autenticado: {} | Tipo: {}", usuarioAutenticado.getEmail(), usuarioAutenticado.getTipo());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (usuarioAutenticado.getTipo() == null) {
            logger.warn("[AUTENTICAR] Usuário sem tipo definido: {}", usuarioAutenticado.getEmail());
            String token = gerenciadorTokenJwt.generateToken(authentication);
            logger.info("[AUTENTICAR] Token gerado para usuário sem tipo: {}", usuarioAutenticado.getEmail());
            return UsuarioMapper.of(usuarioAutenticado, token);
        }

        final String token = gerenciadorTokenJwt.generateToken(authentication);
        logger.info("[AUTENTICAR] Token gerado para usuário: {} | Tipo retornado: {}", usuarioAutenticado.getEmail(), usuarioAutenticado.getTipo().name());

        return UsuarioMapper.of(usuarioAutenticado, token);
    }

    @Override
    public List<UsuarioListarOutput> buscarUsuarios() {
        logger.info("Buscando todos os usuários");
        List<Usuario> usuarios = usuarioRepository.findAll();
        logger.info("Total de usuários encontrados: {}", usuarios.size());

        List<UsuarioListarOutput> resultado = usuarios.stream().map(UsuarioMapper::of).toList();
        logger.debug("Usuários mapeados para saída: {}", resultado);

        return resultado;
    }

    @Override
    public Optional<Usuario> buscaUsuario(Integer id) {
        logger.info("Buscando usuário com ID: {}", id);
        Optional<Usuario> usuario = usuarioRepository.findById(id);

        usuario.ifPresentOrElse(
                usuario1 -> logger.info("Usuário encontrado: ID={}, email={}", usuario1.getIdUsuario(), usuario1.getEmail()),
                () -> logger.warn("Usuário com ID {} não encontrado", id)
        );

        return usuario;
    }

    @Override
    public Optional<Usuario> buscaUsuarioPorNome(String nome) {
        logger.info("Buscando usuário com nome: {}", nome);
        Optional<Usuario> usuario = fichaRepository.findByNome(nome);

        usuario.ifPresentOrElse(
                usuario1 -> logger.info("Usuário encontrado: ID={}, email={}", usuario1.getIdUsuario(), usuario1.getEmail()),
                () -> {
                    logger.error("Erro ao buscar usuário com nome: {}", nome);
                    throw new UsernameNotFoundException(nome);
                }
        );

        return usuario;
    }

    @Override
    public UsuarioListarOutput atualizarUsuario(Integer id, UsuarioInputSegundaFase usuarioInputSegundaFase) {
        logger.info("Atualizando usuário com ID: {}", id);
        logger.debug("Dados recebidos para atualização: {}", usuarioInputSegundaFase);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não cadastrado para ID: " + id));
        logger.info("Usuário encontrado: email={}", usuario.getEmail());

        Ficha ficha = usuario.getFicha();
        logger.info("Genero atual da ficha: {}", ficha.getGenero());

        ficha.setGenero(Genero.fromString(usuarioInputSegundaFase.getGenero()));
        logger.info("Genero atualizado para: {}", ficha.getGenero());

        usuario.setTipo(usuarioInputSegundaFase.getTipo());
        logger.info("Tipo do usuário atualizado para: {}", usuario.getTipo());

        usuarioRepository.save(usuario);
        logger.info("Usuário salvo após atualização: ID={}, email={}", usuario.getIdUsuario(), usuario.getEmail());

        UsuarioListarOutput usuarioListar = UsuarioMapper.of(usuario);
        logger.debug("Dados do usuário após atualização: {}", usuarioListar);

        return usuarioListar;
    }

    @Override
    public void deletarUsuario(Integer id) {
        logger.info("Deletando usuário com ID: {}", id);

        voluntarioService.excluirVoluntario(id);
        logger.info("Voluntário excluído para usuário ID: {}", id);

        usuarioRepository.deleteById(id);
        logger.info("Usuário com ID {} deletado do banco", id);
    }

    @Override
    public Usuario buscarDadosPrimeiraFase(Integer idUsuario) {
        logger.info("Buscando dados da primeira fase do usuário ID: {}", idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para ID: " + idUsuario));
        logger.info("Usuário encontrado: email={}, ficha={}", usuario.getEmail(), usuario.getFicha());
        return usuario;
    }

    @Override
    public Usuario buscarDadosPrimeiraFase(String email) {
        logger.info("Buscando dados da primeira fase do usuário por email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para email: " + email));
        logger.info("Usuário encontrado: ID={}, ficha={}", usuario.getIdUsuario(), usuario.getFicha());
        return usuario;
    }

    @Override
    public Optional<Usuario> buscaUsuarioPorEmail(String email) {
        logger.info("Buscando usuário com email: {}", email);
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

        usuario.ifPresentOrElse(
                usuario1 -> logger.info("Usuário encontrado: ID={}, email={}", usuario1.getIdUsuario(), usuario1.getEmail()),
                () -> logger.warn("Usuário com email {} não encontrado", email)
        );

        return usuario;
    }
}