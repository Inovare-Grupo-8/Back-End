package org.com.imaapi.model.usuario;

import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.usuario.input.UsuarioAutenticacaoInput;
import org.com.imaapi.model.usuario.input.UsuarioInputPrimeiraFase;
import org.com.imaapi.model.usuario.input.UsuarioInputSegundaFase;
import org.com.imaapi.model.usuario.input.VoluntarioInput;
import org.com.imaapi.model.usuario.output.UsuarioDetalhesOutput;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioPrimeiraFaseOutput;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.model.usuario.output.UsuarioClassificacaoOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class UsuarioMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioMapper.class);    public static Usuario of(UsuarioInputPrimeiraFase usuarioInputPrimeiraFase, UsuarioInputSegundaFase usuarioInputSegundaFase) {
        Usuario usuario = new Usuario();

        Ficha ficha = new Ficha();
        ficha.setNome(usuarioInputPrimeiraFase.getNome());
        ficha.setSobrenome(usuarioInputPrimeiraFase.getSobrenome());
        ficha.setCpf(usuarioInputPrimeiraFase.getCpf());
        ficha.setDtNascim(usuarioInputSegundaFase.getDataNascimento());
        usuario.setEmail(usuarioInputPrimeiraFase.getEmail());
        usuario.setSenha(usuarioInputPrimeiraFase.getSenha());
        usuario.setTipo(TipoUsuario.NAO_CLASSIFICADO);
        usuario.setFicha(ficha);

        return usuario;
    }public static Usuario of(UsuarioAutenticacaoInput usuarioAutenticacaoInput) {
//        LOGGER.debug("[USUARIO_MAPPER] Criando Usuario a partir de UsuarioAutenticacaoInput para email: {}",
//                usuarioAutenticacaoInput.getEmail());
                
        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioAutenticacaoInput.getEmail());
        usuario.setSenha(usuarioAutenticacaoInput.getSenha());
        
//        LOGGER.debug("[USUARIO_MAPPER] Usuario criado com email: {}", usuario.getEmail());
        return usuario;
    }

    public static UsuarioTokenOutput of(Usuario usuario, String token) {
        UsuarioTokenOutput usuarioTokenOutput = new UsuarioTokenOutput();
        usuarioTokenOutput.setIdUsuario(usuario.getIdUsuario());
        usuarioTokenOutput.setNome(usuario.getFicha().getNome());
        usuarioTokenOutput.setEmail(usuario.getEmail());
        usuarioTokenOutput.setToken(token);
        usuarioTokenOutput.setTipo(usuario.getTipo());
        return usuarioTokenOutput;
    }

    public static UsuarioListarOutput of(Usuario usuario) {
        UsuarioListarOutput output = new UsuarioListarOutput();
        output.setIdUsuario(usuario.getIdUsuario());
        output.setNome(usuario.getFicha().getNome());
        output.setEmail(usuario.getEmail());
        output.setTipo(usuario.getTipo());
        return output;
    }

    public static VoluntarioInput of(UsuarioInputSegundaFase usuarioInputSegundaFase, Integer idUsuario) {
        VoluntarioInput voluntario = new VoluntarioInput();
        voluntario.setFkUsuario(idUsuario);
        voluntario.setFuncao(usuarioInputSegundaFase.getFuncao());
        return voluntario;
    }    public static UsuarioPrimeiraFaseOutput ofPrimeiraFase(Usuario usuario) {
        UsuarioPrimeiraFaseOutput output = new UsuarioPrimeiraFaseOutput();
        output.setIdUsuario(usuario.getIdUsuario());
        output.setNome(usuario.getFicha().getNome());
        output.setSobrenome(usuario.getFicha().getSobrenome());
        output.setEmail(usuario.getEmail());
        output.setCpf(usuario.getFicha().getCpf());
        output.setDataNascimento(usuario.getFicha().getDtNascim());
        return output;
    }
  
  public static UsuarioDetalhesOutput ofDetalhes(Usuario usuario, Ficha ficha) {
//        LOGGER.info("[USUARIO_MAPPER] Criando UsuarioDetalhesOutput para autenticação do usuário: {}", usuario.getEmail());
//        LOGGER.debug("[USUARIO_MAPPER] Ficha associada: ID={}", ficha != null ? ficha.getIdFicha() : "null");
//        LOGGER.debug("[USUARIO_MAPPER] Tipo do usuário: {}", usuario.getTipo());
        
        UsuarioDetalhesOutput details = new UsuarioDetalhesOutput(usuario, ficha);
//        LOGGER.debug("[USUARIO_MAPPER] Autoridades concedidas: {}", details.getAuthorities());
        
        return details;
    }    public static UsuarioClassificacaoOutput ofClassificacao(Usuario usuario) {
        return ofClassificacao(usuario, null);
    }

    public static UsuarioClassificacaoOutput ofClassificacao(Usuario usuario, List<Telefone> telefones) {
        UsuarioClassificacaoOutput output = new UsuarioClassificacaoOutput();
        
        // Dados básicos do usuário
        output.setId(usuario.getIdUsuario());
        output.setEmail(usuario.getEmail());
        output.setTipo(usuario.getTipo());
        output.setDataCadastro(usuario.getDataCadastro());
        
        // Dados da ficha
        if (usuario.getFicha() != null) {
            Ficha ficha = usuario.getFicha();
            output.setNome(ficha.getNome());
            output.setSobrenome(ficha.getSobrenome());            output.setCpf(ficha.getCpf());            output.setDataNascimento(ficha.getDtNascim());
            output.setRendaMinima(ficha.getRendaMinima());
            output.setRendaMaxima(ficha.getRendaMaxima());
            output.setGenero(ficha.getGenero());
            output.setAreaInteresse(ficha.getAreaOrientacao());
            output.setProfissao(ficha.getProfissao());
            
            // Dados do endereço
            if (ficha.getEndereco() != null) {
                Endereco endereco = ficha.getEndereco();
                output.setLogradouro(endereco.getLogradouro());
                output.setNumero(endereco.getNumero());
                output.setComplemento(endereco.getComplemento());
                output.setBairro(endereco.getBairro());
                output.setCidade(endereco.getCidade());
                output.setUf(endereco.getUf());
                output.setCep(endereco.getCep());
            }
            
            // Telefones
            if (telefones != null && !telefones.isEmpty()) {
                List<UsuarioClassificacaoOutput.TelefoneOutput> telefonesOutput = telefones.stream()
                    .map(telefone -> new UsuarioClassificacaoOutput.TelefoneOutput(
                        formatarNumeroTelefone(telefone),
                        telefone.getWhatsapp() != null && telefone.getWhatsapp() ? "WhatsApp" : "Telefone"
                    ))
                    .collect(Collectors.toList());
                output.setTelefones(telefonesOutput);
            }
        }
        
        return output;
    }

    private static String formatarNumeroTelefone(Telefone telefone) {
        StringBuilder numero = new StringBuilder();
        if (telefone.getDdd() != null) {
            numero.append("(").append(telefone.getDdd()).append(") ");
        }
        if (telefone.getPrefixo() != null) {
            numero.append(telefone.getPrefixo());
        }
        if (telefone.getSufixo() != null) {
            if (telefone.getPrefixo() != null) {
                numero.append("-");
            }
            numero.append(telefone.getSufixo());
        }
        return numero.toString();
    }

    public static List<UsuarioClassificacaoOutput> ofClassificacaoList(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(usuario -> ofClassificacao(usuario))
                .collect(Collectors.toList());
    }
}
