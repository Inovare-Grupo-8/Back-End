package org.com.imaapi.service.impl;

import org.com.imaapi.config.GerenciadorTokenJwt;
import org.com.imaapi.model.enums.Genero;
import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.usuario.*;
import org.com.imaapi.model.usuario.input.UsuarioInputPrimeiraFase;
import org.com.imaapi.model.usuario.input.UsuarioInputSegundaFase;
import org.com.imaapi.model.usuario.input.VoluntarioInput;
import org.com.imaapi.model.usuario.output.*;
import org.com.imaapi.repository.*;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Autowired
    private TelefoneRepository telefoneRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private VoluntarioService voluntarioService;

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private GerenciadorTokenJwt gerenciadorTokenJwt;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private FichaRepository fichaRepository;

    @Autowired
    private EnderecoService enderecoService;

    @Override
    public Usuario cadastrarPrimeiraFase(UsuarioInputPrimeiraFase usuarioInputPrimeiraFase) {
        logger.info("Iniciando cadastro da primeira fase do usuário. Dados recebidos: {}", usuarioInputPrimeiraFase);

        // Validar se CPF já existe
        String cpfInput = usuarioInputPrimeiraFase.getCpf().replaceAll("\\D", "");
        Optional<Ficha> fichaExistente = fichaRepository.findByCpf(cpfInput);
        if (fichaExistente.isPresent()) {
            logger.warn("Tentativa de cadastro com CPF já existente: {}", cpfInput);
            throw new IllegalArgumentException("CPF já cadastrado no sistema");
        }

        String senhaCriptografada = passwordEncoder.encode(usuarioInputPrimeiraFase.getSenha());
        logger.debug("Senha criptografada para o email {}: {}", usuarioInputPrimeiraFase.getEmail(), senhaCriptografada);

        Ficha ficha = new Ficha();
        ficha.setNome(usuarioInputPrimeiraFase.getNome());
        ficha.setSobrenome(usuarioInputPrimeiraFase.getSobrenome());
        ficha.setCpf(cpfInput);
        logger.info("Ficha criada com nome: {} e CPF: {}", ficha.getNome(), cpfInput);

        Ficha fichaSalva = fichaRepository.save(ficha);
        logger.info("Ficha salva com ID: {}", fichaSalva.getIdFicha());

        Usuario novoUsuario = Usuario.criarUsuarioBasico(
                usuarioInputPrimeiraFase.getEmail(),
                senhaCriptografada,
                fichaSalva);

        logger.info("Usuário básico criado com email: {} e ficha associada com nome: {}",
                novoUsuario.getEmail(),
                novoUsuario.getFicha().getNome());

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        logger.info("Usuário fase 1 salvo no banco com ID: {} e email: {}", usuarioSalvo.getIdUsuario(), usuarioSalvo.getEmail());

        emailService.enviarEmail(usuarioSalvo.getEmail(),
                usuarioSalvo.getFicha().getNome() + "|" + usuarioSalvo.getIdUsuario(),
                "continuar cadastro");
        logger.info("Email enviado para o usuário {} para continuar cadastro", usuarioSalvo.getEmail());
        return usuarioSalvo;
    }

    @Override
    public Usuario cadastrarPrimeiraFaseVoluntario(UsuarioInputPrimeiraFase usuarioInputPrimeiraFase) {
        logger.info("Iniciando cadastro da primeira fase do voluntário. Dados recebidos: {}", usuarioInputPrimeiraFase);

        // Validar se CPF já existe
        String cpfInput = usuarioInputPrimeiraFase.getCpf().replaceAll("\\D", "");
        Optional<Ficha> fichaExistente = fichaRepository.findByCpf(cpfInput);
        if (fichaExistente.isPresent()) {
            logger.warn("Tentativa de cadastro com CPF já existente: {}", cpfInput);
            throw new IllegalArgumentException("CPF já cadastrado no sistema");
        }

        String senhaCriptografada = passwordEncoder.encode(usuarioInputPrimeiraFase.getSenha());
        logger.debug("Senha criptografada para o email {}: {}", usuarioInputPrimeiraFase.getEmail(), senhaCriptografada);

        Ficha ficha = new Ficha();
        ficha.setNome(usuarioInputPrimeiraFase.getNome());
        ficha.setSobrenome(usuarioInputPrimeiraFase.getSobrenome());
        ficha.setCpf(cpfInput);
        logger.info("Ficha criada com nome: {} e CPF: {}", ficha.getNome(), cpfInput);

        Ficha fichaSalva = fichaRepository.save(ficha);
        logger.info("Ficha salva com ID: {}", fichaSalva.getIdFicha());

        Usuario novoVoluntario = Usuario.criarVoluntario(
                usuarioInputPrimeiraFase.getEmail(),
                senhaCriptografada,
                fichaSalva);

        logger.info("Voluntário criado com email: {} e ficha associada com nome: {}",
                novoVoluntario.getEmail(),
                novoVoluntario.getFicha().getNome());

        Usuario voluntarioSalvo = usuarioRepository.save(novoVoluntario);
        logger.info("Voluntário fase 1 salvo no banco com ID: {} e email: {} e tipo: {}",
                voluntarioSalvo.getIdUsuario(), voluntarioSalvo.getEmail(), voluntarioSalvo.getTipo());

        emailService.enviarEmail(voluntarioSalvo.getEmail(),
                voluntarioSalvo.getFicha().getNome() + "|" + voluntarioSalvo.getIdUsuario(),
                "continuar cadastro");
        logger.info("Email enviado para o voluntário {} para continuar cadastro", voluntarioSalvo.getEmail());

        return voluntarioSalvo;
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

    @Override    public Usuario cadastrarSegundaFaseVoluntario(Integer idUsuario, UsuarioInputSegundaFase usuarioInputSegundaFase) {
        logger.info("Iniciando cadastro fase 2 para voluntário ID: {}", idUsuario);
        if (usuarioInputSegundaFase.getFuncao() == null) {
            throw new IllegalArgumentException("A função do voluntário deve ser informada");
        }

        if (usuarioInputSegundaFase.getRendaMinima() == null || usuarioInputSegundaFase.getRendaMaxima() == null) {
            throw new IllegalArgumentException("A faixa de renda do voluntário deve ser informada");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para ID: " + idUsuario));

        usuario = cadastrarSegundaFase(idUsuario, usuarioInputSegundaFase);
        usuario.atualizarTipo(TipoUsuario.VOLUNTARIO);
        usuarioRepository.save(usuario);
        logger.info("Tipo do usuário atualizado para VOLUNTARIO e salvo: ID={}, email={}", usuario.getIdUsuario(), usuario.getEmail());
        VoluntarioInput voluntarioInput = UsuarioMapper.of(usuarioInputSegundaFase, idUsuario);

        // Para segunda fase de voluntário, verificar se já existe um voluntário e atualizar em vez de criar
        Voluntario voluntarioExistente = voluntarioRepository.findByUsuario_IdUsuario(idUsuario);
        if (voluntarioExistente != null) {
            logger.info("Voluntário já existe para usuário ID: {}, atualizando dados...", idUsuario);
            voluntarioService.atualizarVoluntario(voluntarioInput);
            logger.info("Voluntário atualizado com sucesso para usuário ID: {}", idUsuario);
        } else {
            logger.info("Voluntário não existe para usuário ID: {}, criando novo...", idUsuario);
            voluntarioService.cadastrarVoluntario(voluntarioInput);
            logger.info("Voluntário cadastrado com sucesso para usuário ID: {}", idUsuario);
        }

        emailService.enviarEmail(usuario.getEmail(), usuario.getFicha().getNome(), "bem vindo voluntario");
        logger.info("Email de boas-vindas para voluntário enviado para o usuário: {}", usuario.getEmail());

        return usuario;
    }

    @Override
    public UsuarioTokenOutput autenticar(Usuario usuario, Ficha ficha, String senha) {
        logger.info("[AUTENTICAR] Iniciando autenticação para email: {}", usuario.getEmail());
//        logger.debug("[AUTENTICAR] Senha raw do usuário (primeiros 4 caracteres): {}",
//                senha.length() > 4 ? senha.substring(0, 4) + "..." : "***");
//        logger.debug("[AUTENTICAR] Ficha associada ao usuário: ID={}",
//                ficha != null ? ficha.getIdFicha() : "null");

        try {
//            logger.debug("[AUTENTICAR] Criando token de autenticação com email: {}", usuario.getEmail());
            UsuarioDetalhesOutput usuarioDetalhes = UsuarioMapper.ofDetalhes(usuario, ficha);
//            logger.debug("[AUTENTICAR] Autoridades do usuário: {}", usuarioDetalhes.getAuthorities());

            final UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                    usuario.getEmail(), senha, usuarioDetalhes.getAuthorities());
//            logger.debug("[AUTENTICAR] Token de autenticação criado com sucesso para: {}", usuario.getEmail());

//            logger.info("[AUTENTICAR] Enviando credenciais para AuthenticationManager...");
            final Authentication authentication = authenticationManager.authenticate(credentials);
//            logger.info("[AUTENTICAR] AuthenticationManager autenticou as credenciais para: {}", usuario.getEmail());

            Usuario usuarioAutenticado = usuarioRepository.findByEmail(usuario.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não cadastrado: " + usuario.getEmail()));

            logger.info("[AUTENTICAR] Usuário autenticado: {} | Tipo: {}",
                    usuarioAutenticado.getEmail(), usuarioAutenticado.getTipo());
            SecurityContextHolder.getContext().setAuthentication(authentication);
//            logger.debug("[AUTENTICAR] Contexto de segurança configurado com sucesso");

            if (usuarioAutenticado.getTipo() == null) {
                logger.warn("[AUTENTICAR] Usuário sem tipo definido: {}", usuarioAutenticado.getEmail());
                String token = gerenciadorTokenJwt.generateToken(authentication);
                logger.info("[AUTENTICAR] Token gerado para usuário sem tipo: {}", usuarioAutenticado.getEmail());
                return UsuarioMapper.of(usuarioAutenticado, token);
            }

            final String token = gerenciadorTokenJwt.generateToken(authentication);
            logger.info("[AUTENTICAR] Token gerado para usuário: {} | Tipo retornado: {}", usuarioAutenticado.getEmail(), usuarioAutenticado.getTipo().name());

            return UsuarioMapper.of(usuarioAutenticado, token);
        } catch (Exception e) {
            logger.error("[AUTENTICAR] Erro durante autenticação: {}", e.getMessage(), e);
            throw e;
        }
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
    public Optional<Usuario> buscaUsuarioPorNome(String termo) {
        logger.info("Buscando usuário com nome ou sobrenome contendo: {}", termo);
        List<Usuario> usuarios = fichaRepository.findByNomeOrSobrenomeContaining(termo);

        if (usuarios.isEmpty()) {
            logger.warn("Nenhum usuário encontrado com nome ou sobrenome contendo: {}", termo);
            return Optional.empty();
        }

        Usuario primeiroUsuario = usuarios.get(0);
        logger.info("Usuário encontrado: ID={}, email={}", primeiroUsuario.getIdUsuario(), primeiroUsuario.getEmail());

        return Optional.of(primeiroUsuario);
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
        logger.info("Iniciando deleção do usuário com ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com ID: " + id));

        voluntarioService.excluirVoluntario(id);
        logger.info("Voluntário excluído para usuário ID: {}", id);

        Ficha ficha = usuario.getFicha();
        if (ficha != null) {
            telefoneRepository.deleteByFichaIdFicha(ficha.getIdFicha());
            logger.info("Telefones excluídos para a ficha ID: {}", ficha.getIdFicha());

            usuarioRepository.delete(usuario);
            logger.info("Usuário com ID {} deletado do banco", id);

            fichaRepository.delete(ficha);
            logger.info("Ficha ID: {} excluída", ficha.getIdFicha());
        } else {
            usuarioRepository.delete(usuario);
            logger.info("Usuário com ID {} deletado do banco", id);
        }
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
        logger.info("Buscando usuário por email: {}", email);
        return usuarioRepository.findByEmail(email);
    }    // Implementação dos novos métodos para classificação de usuários

    @Override
    public List<UsuarioClassificacaoOutput> buscarUsuariosNaoClassificados() {
        logger.info("Buscando usuários não classificados");
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Filtrar usuários com tipo NAO_CLASSIFICADO e mapear para DTO completo
        List<UsuarioClassificacaoOutput> usuariosNaoClassificados = usuarios.stream()
                .filter(usuario -> usuario.getTipo() == TipoUsuario.NAO_CLASSIFICADO)
                .map(usuario -> {
                    // Buscar telefones para este usuário
                    List<Telefone> telefones = Collections.emptyList();
                    if (usuario.getFicha() != null) {
                        telefones = telefoneRepository.findByFichaIdFicha(usuario.getFicha().getIdFicha());
                    }
                    return UsuarioMapper.ofClassificacao(usuario, telefones);
                })
                .toList();

        logger.info("Total de usuários não classificados encontrados: {}", usuariosNaoClassificados.size());
        return usuariosNaoClassificados;
    }

    @Override
    public UsuarioListarOutput classificarUsuarioComoGratuidade(Integer id) {
        logger.info("Classificando usuário ID {} como GRATUIDADE", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para ID: " + id));

        // Verificar se o usuário está realmente como NAO_CLASSIFICADO
        if (usuario.getTipo() != TipoUsuario.NAO_CLASSIFICADO) {
            logger.warn("Tentativa de classificar usuário ID {} que não está como NAO_CLASSIFICADO. Tipo atual: {}",
                    id, usuario.getTipo());
            throw new IllegalStateException("Usuário não está pendente de classificação");
        }

        // Atualizar o tipo para GRATUIDADE
        usuario.atualizarTipo(TipoUsuario.GRATUIDADE);
        usuarioRepository.save(usuario);

        logger.info("Usuário ID {} classificado como GRATUIDADE com sucesso", id);

        return UsuarioMapper.of(usuario);
    }

    @Override
    public UsuarioListarOutput classificarUsuarioComoValorSocial(Integer id) {
        logger.info("Classificando usuário ID {} como VALOR_SOCIAL", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para ID: " + id));

        // Verificar se o usuário está realmente como NAO_CLASSIFICADO
        if (usuario.getTipo() != TipoUsuario.NAO_CLASSIFICADO) {
            logger.warn("Tentativa de classificar usuário ID {} que não está como NAO_CLASSIFICADO. Tipo atual: {}",
                    id, usuario.getTipo());
            throw new IllegalStateException("Usuário não está pendente de classificação");
        }

        // Atualizar o tipo para VALOR_SOCIAL
        usuario.atualizarTipo(TipoUsuario.VALOR_SOCIAL);
        usuarioRepository.save(usuario);

        logger.info("Usuário ID {} classificado como VALOR_SOCIAL com sucesso", id);
        return UsuarioMapper.of(usuario);
    }

    @Override
    public String enviarCredenciaisVoluntario(String email, String nome, String senha) {
        logger.info("Enviando credenciais para voluntário {} com email: {}", nome, email);

        try {
            // O EmailService espera as credenciais no formato "nome|email|senha"
            String credenciaisString = nome + "|" + email + "|" + senha;
            String resultado = emailService.enviarEmail(email, credenciaisString, "credenciais voluntario");
            logger.info("Credenciais enviadas com sucesso para o email: {}", email);
            return resultado;
        } catch (Exception e) {
            logger.error("Erro ao enviar credenciais para o email: {}", email, e);
            throw new RuntimeException("Erro ao enviar email com credenciais: " + e.getMessage());
        }
    }

    @Override
    public List<VoluntarioListagemOutput> listarVoluntarios() {
        logger.info("Listando todos os voluntários cadastrados");

        try {
            List<Voluntario> voluntarios = voluntarioRepository.findAll();
            logger.info("Total de voluntários encontrados: {}", voluntarios.size());

            List<VoluntarioListagemOutput> resultado = voluntarios.stream()
                    .map(voluntario -> {
                        VoluntarioListagemOutput output = new VoluntarioListagemOutput();

                        // Dados básicos do voluntário
                        output.setIdUsuario(voluntario.getFkUsuario());
                        output.setIdVoluntario(voluntario.getIdVoluntario());
                        output.setDataCadastro(voluntario.getDataCadastro());

                        // Função (especialidade)
                        if (voluntario.getFuncao() != null) {
                            output.setFuncao(voluntario.getFuncao().toString());
                        }

                        // Buscar dados do usuário associado
                        if (voluntario.getUsuario() != null) {
                            Usuario usuario = voluntario.getUsuario();
                            output.setEmail(usuario.getEmail());
                            output.setUltimoAcesso(usuario.getUltimoAcesso());

                            // Calcular se está ativo (últimos 30 dias)
                            boolean ativo = false;
                            if (usuario.getUltimoAcesso() != null) {
                                LocalDateTime trintaDiasAtras = LocalDateTime.now().minusDays(30);
                                ativo = usuario.getUltimoAcesso().isAfter(trintaDiasAtras);
                            }
                            output.setAtivo(ativo);

                            // Dados da ficha
                            if (usuario.getFicha() != null) {
                                Ficha ficha = usuario.getFicha();
                                output.setNome(ficha.getNome());
                                output.setSobrenome(ficha.getSobrenome());
                                output.setAreaOrientacao(ficha.getAreaOrientacao());
                            }
                        }

                        return output;
                    })
                    .toList();

            logger.info("Voluntários mapeados com sucesso: {} registros", resultado.size());
            return resultado;

        } catch (Exception e) {
            logger.error("Erro ao listar voluntários: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar lista de voluntários: " + e.getMessage());
        }
    }

    @Override
    public void atualizarUltimoAcesso(Integer idUsuario) {
        logger.info("Atualizando último acesso para usuário ID: {}", idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para ID: " + idUsuario));

        usuario.setUltimoAcesso(LocalDateTime.now());
        usuarioRepository.save(usuario);
        logger.info("Último acesso atualizado com sucesso para usuário ID: {}", idUsuario);
    }
}