package org.com.imaapi.service.impl;

import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.Voluntario;
import org.com.imaapi.model.usuario.input.UsuarioInputAtualizacaoDadosPessoais;
import org.com.imaapi.model.usuario.input.UsuarioInputPrimeiraFase;
import org.com.imaapi.model.usuario.input.VoluntarioDadosProfissionaisInput;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.model.usuario.output.UsuarioDadosPessoaisOutput;
import org.com.imaapi.model.usuario.output.UsuarioOutput;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.repository.VoluntarioRepository;
import org.com.imaapi.service.EnderecoService;
import org.com.imaapi.service.FotoService;
import org.com.imaapi.service.PerfilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class PerfilServiceImpl implements PerfilService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerfilServiceImpl.class);

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EnderecoService enderecoService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FotoService fotoService;
    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Override
    public UsuarioDadosPessoaisOutput buscarDadosPessoaisPorId(Integer usuarioId) {
        LOGGER.info("Buscando dados pessoais para o usuário com ID: {}", usuarioId);
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        if (usuario == null) {
            LOGGER.warn("Usuário não encontrado para o ID: {}", usuarioId);
            return null;
        }
        UsuarioDadosPessoaisOutput usuarioOutput = new UsuarioDadosPessoaisOutput();
        usuarioOutput.setNome(usuario.getNome());
        usuarioOutput.setCpf(usuario.getCpf());
        usuarioOutput.setEmail(usuario.getEmail());
        usuarioOutput.setDataNascimento(usuario.getDataNascimento());
        usuarioOutput.setTipo(usuario.getTipo().toString());
        LOGGER.info("Dados pessoais encontrados para o usuário com ID: {}", usuarioId);
        return usuarioOutput;
    }

    public EnderecoOutput buscarEnderecoPorId(Integer usuarioId) {
        LOGGER.info("Buscando endereço para o usuário com ID: {}", usuarioId);
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        if (usuario != null && usuario.getEndereco() != null) {
            EnderecoOutput enderecoOutput = new EnderecoOutput();
            enderecoOutput.setCep(usuario.getEndereco().getCep());
            enderecoOutput.setNumero(usuario.getEndereco().getNumero());
            enderecoOutput.setComplemento(usuario.getEndereco().getComplemento());
            enderecoOutput.setLogradouro(usuario.getEndereco().getLogradouro());
            enderecoOutput.setBairro(usuario.getEndereco().getBairro());
            enderecoOutput.setLocalidade(usuario.getEndereco().getLocalidade());
            enderecoOutput.setUf(usuario.getEndereco().getUf());
            return enderecoOutput;
        }
        return null;
    }

    @Override
    public UsuarioOutput atualizarDadosPessoais(Integer usuarioId, UsuarioInputAtualizacaoDadosPessoais usuarioInputAtualizacaoDadosPessoais) {
        LOGGER.info("Atualizando dados pessoais para o usuário com ID: {}", usuarioId);
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        if (usuario == null) {
            LOGGER.warn("Usuário não encontrado para o ID: {}", usuarioId);
            return null;
        }

        if (usuarioInputAtualizacaoDadosPessoais.getNome() != null) {
            usuario.setNome(usuarioInputAtualizacaoDadosPessoais.getNome());
        }
        if (usuarioInputAtualizacaoDadosPessoais.getEmail() != null) {
            usuario.setEmail(usuarioInputAtualizacaoDadosPessoais.getEmail());
        }
        if (usuarioInputAtualizacaoDadosPessoais.getSenha() != null) {
            String senhaCriptografada = passwordEncoder.encode(usuarioInputAtualizacaoDadosPessoais.getSenha());
            usuario.setSenha(senhaCriptografada);
        }
        if (usuarioInputAtualizacaoDadosPessoais.getDataNascimento() != null) {
            usuario.setDataNascimento(usuarioInputAtualizacaoDadosPessoais.getDataNascimento());
        }

        usuarioRepository.save(usuario);
        LOGGER.info("Dados pessoais atualizados com sucesso para o usuário com ID: {}", usuarioId);

        UsuarioDadosPessoaisOutput dadosPessoais = buscarDadosPessoaisPorId(usuarioId);
        return converterParaUsuarioOutput(dadosPessoais);
    }

    @Override
    public boolean atualizarEnderecoPorUsuarioId(Integer usuarioId, String cep, String numero, String complemento) {
        LOGGER.info("Iniciando atualização de endereço para o usuário com ID: {}", usuarioId);
        try {
            Usuario usuario = buscarUsuarioPorId(usuarioId);
            if (usuario == null || usuario.getEndereco() == null) {
                LOGGER.warn("Usuário ou endereço não encontrado para o ID: {}", usuarioId);
                return false;
            }

            if (!cep.equals(usuario.getEndereco().getCep())) {
                EnderecoOutput enderecoApi = enderecoService.buscaEndereco(cep, numero, complemento).getBody();
                if (enderecoApi == null) {
                    LOGGER.warn("Endereço não encontrado na API para o CEP: {}", cep);
                    return false;
                }
                usuario.getEndereco().setCep(enderecoApi.getCep());
                usuario.getEndereco().setLogradouro(enderecoApi.getLogradouro());
                usuario.getEndereco().setBairro(enderecoApi.getBairro());
                usuario.getEndereco().setLocalidade(enderecoApi.getLocalidade());
                usuario.getEndereco().setUf(enderecoApi.getUf());
            }

            usuario.getEndereco().setNumero(numero);
            usuario.getEndereco().setComplemento(complemento);
            usuarioRepository.save(usuario);

            LOGGER.info("Endereço atualizado com sucesso para o usuário com ID: {}", usuarioId);
            return true;
        } catch (Exception e) {
            LOGGER.error("Erro ao atualizar endereço para o usuário com ID: {}", usuarioId, e);
            return false;
        }
    }

    @Override
    public String salvarFoto(Integer usuarioId, String tipo, MultipartFile file) throws IOException {
        LOGGER.info("Salvando foto para o usuário com ID: {}", usuarioId);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        String fotoUrl = fotoService.salvarFoto(tipo, usuarioId, file);
        usuario.setFotoUrl(fotoUrl);
        usuarioRepository.save(usuario);

        LOGGER.info("Foto salva com sucesso para o usuário com ID: {}", usuarioId);
        return fotoUrl;
    }

    @Override
    public boolean atualizarDadosProfissionais(Integer usuarioId, VoluntarioDadosProfissionaisInput dadosProfissionais) {
        LOGGER.info("Atualizando dados profissionais para o voluntário com ID de usuário: {}", usuarioId);
        Voluntario voluntario = voluntarioRepository.findByUsuario_IdUsuario(usuarioId);
        if (voluntario == null) {
            LOGGER.warn("Voluntário não encontrado para o ID de usuário: {}", usuarioId);
            return false;
        }

        if (dadosProfissionais.getFuncao() != null) {
            voluntario.setFuncao(dadosProfissionais.getFuncao());
        }
        if (dadosProfissionais.getRegistroProfissional() != null) {
            voluntario.setRegistroProfissional(dadosProfissionais.getRegistroProfissional());
        }
        if (dadosProfissionais.getBiografiaProfissional() != null) {
            voluntario.setBiografiaProfissional(dadosProfissionais.getBiografiaProfissional());
        }

        voluntarioRepository.save(voluntario);
        LOGGER.info("Dados profissionais atualizados com sucesso para o voluntário com ID de usuário: {}", usuarioId);
        return true;
    }

    @Override
    public boolean criarDisponibilidade(Integer usuarioId, Map<String, Object> disponibilidade) {
        LOGGER.info("Criando disponibilidade para o voluntário com ID de usuário: {}", usuarioId);
        Voluntario voluntario = voluntarioRepository.findByUsuario_IdUsuario(usuarioId);
        if (voluntario == null) {
            LOGGER.warn("Voluntário não encontrado para o ID de usuário: {}", usuarioId);
            return false;
        }

        // Lógica para criar disponibilidade (exemplo: salvar no banco)
        // Exemplo: voluntario.setDisponibilidade(disponibilidade);

        voluntarioRepository.save(voluntario);
        LOGGER.info("Disponibilidade criada com sucesso para o voluntário com ID de usuário: {}", usuarioId);
        return true;
    }

    @Override
    public boolean atualizarDisponibilidade(Integer usuarioId, Map<String, Object> disponibilidade) {
        LOGGER.info("Atualizando disponibilidade para o voluntário com ID de usuário: {}", usuarioId);
        Voluntario voluntario = voluntarioRepository.findByUsuario_IdUsuario(usuarioId);
        if (voluntario == null) {
            LOGGER.warn("Voluntário não encontrado para o ID de usuário: {}", usuarioId);
            return false;
        }

        // Lógica para atualizar disponibilidade (exemplo: atualizar campos específicos)
        // Exemplo: voluntario.setDisponibilidade(disponibilidade);

        voluntarioRepository.save(voluntario);
        LOGGER.info("Disponibilidade atualizada com sucesso para o voluntário com ID de usuário: {}", usuarioId);
        return true;
    }

    private Usuario buscarUsuarioPorId(Integer usuarioId) {
        LOGGER.debug("Consultando repositório para o usuário com ID: {}", usuarioId);
        return usuarioRepository.findById(usuarioId).orElse(null);
    }

    private UsuarioOutput converterParaUsuarioOutput(UsuarioDadosPessoaisOutput dadosPessoais) {
        if (dadosPessoais == null) {
            LOGGER.error("Erro: Dados pessoais não podem ser nulos.");
            throw new IllegalArgumentException("Dados pessoais não podem ser nulos.");
        }
        return new UsuarioOutput(
                dadosPessoais.getNome(),
                dadosPessoais.getCpf(),
                dadosPessoais.getEmail(),
                dadosPessoais.getDataNascimento(),
                TipoUsuario.valueOf(dadosPessoais.getTipo())
        );
    }

//    private UsuarioOutput converterParaUsuarioOutput(Usuario usuario) {
//        UsuarioOutput usuarioOutput = new UsuarioOutput(usuario);
//        usuarioOutput.setNome(usuario.getNome());
//        usuarioOutput.setCpf(usuario.getCpf());
//        usuarioOutput.setEmail(usuario.getEmail());
//        usuarioOutput.setDataNascimento(usuario.getDataNascimento());
//        usuarioOutput.setTipo(usuario.getTipo());
//        if (usuario.getEndereco() != null) {
//            EnderecoOutput enderecoOutput = new EnderecoOutput();
//            enderecoOutput.setCep(usuario.getEndereco().getCep());
//            enderecoOutput.setNumero(usuario.getEndereco().getNumero());
//            enderecoOutput.setComplemento(usuario.getEndereco().getComplemento());
//            enderecoOutput.setLogradouro(usuario.getEndereco().getLogradouro());
//            enderecoOutput.setBairro(usuario.getEndereco().getBairro());
//            enderecoOutput.setLocalidade(usuario.getEndereco().getLocalidade());
//            enderecoOutput.setUf(usuario.getEndereco().getUf());
//            usuarioOutput.setEndereco(enderecoOutput);
//        }
//        return usuarioOutput;
//    }
}