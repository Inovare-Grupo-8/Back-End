package org.com.imaapi.model.usuario;

import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.usuario.input.*;
import org.com.imaapi.model.usuario.output.UsuarioDetalhesOutput;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.model.usuario.output.UsuarioPrimeiraFaseOutput;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.repository.VoluntarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioMapper {
    private static VoluntarioRepository voluntarioRepository;

    @Autowired
    public void setVoluntarioRepository(VoluntarioRepository voluntarioRepository) {
        UsuarioMapper.voluntarioRepository = voluntarioRepository;
    }

    public static Usuario of(UsuarioInputPrimeiraFase usuarioInputPrimeiraFase, UsuarioInputSegundaFase usuarioInputSegundaFase) {
        Usuario usuario = new Usuario();

        Ficha ficha = new Ficha();
        ficha.setNome(usuarioInputPrimeiraFase.getNome());
        ficha.setCpf(usuarioInputSegundaFase.getCpf());
        ficha.setDtNascim(usuarioInputSegundaFase.getDataNascimento());

        usuario.setEmail(usuarioInputPrimeiraFase.getEmail());
        usuario.setSenha(usuarioInputPrimeiraFase.getSenha());
        usuario.setTipo(TipoUsuario.NAO_CLASSIFICADO);
        usuario.setFicha(ficha);

        return usuario;
    }

    public static Usuario of(UsuarioAutenticacaoInput usuarioAutenticacaoInput) {
        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioAutenticacaoInput.getEmail());
        usuario.setSenha(usuarioAutenticacaoInput.getSenha());
        return usuario;
    }    public static UsuarioTokenOutput of(Usuario usuario, String token) {
        UsuarioTokenOutput usuarioTokenOutput = new UsuarioTokenOutput();
        // Basic user info
        usuarioTokenOutput.setIdUsuario(usuario.getIdUsuario());
        usuarioTokenOutput.setEmail(usuario.getEmail());
        usuarioTokenOutput.setTipo(usuario.getTipo());
        usuarioTokenOutput.setToken(token);
        usuarioTokenOutput.setDataCadastro(usuario.getDataCadastro());
        usuarioTokenOutput.setCriadoEm(usuario.getCriadoEm());
        usuarioTokenOutput.setAtualizadoEm(usuario.getAtualizadoEm());
        usuarioTokenOutput.setVersao(usuario.getVersao());

        // Ficha info
        Ficha ficha = usuario.getFicha();
        if (ficha != null) {
            usuarioTokenOutput.setIdFicha(ficha.getIdFicha());
            usuarioTokenOutput.setNome(ficha.getNome());
            usuarioTokenOutput.setSobrenome(ficha.getSobrenome());
            usuarioTokenOutput.setCpf(ficha.getCpf());
            usuarioTokenOutput.setDataNascimento(ficha.getDtNascim());
            usuarioTokenOutput.setRenda(ficha.getRenda() != null ? ficha.getRenda().doubleValue() : null);
            usuarioTokenOutput.setGenero(ficha.getGenero());
            usuarioTokenOutput.setProfissao(ficha.getProfissao());

            // Endereco info
            if (ficha.getEndereco() != null) {
                EnderecoOutput enderecoOutput = new EnderecoOutput();
                enderecoOutput.setCep(ficha.getEndereco().getCep());
                enderecoOutput.setLogradouro(ficha.getEndereco().getLogradouro());
                enderecoOutput.setComplemento(ficha.getEndereco().getComplemento());
                enderecoOutput.setBairro(ficha.getEndereco().getBairro());
                enderecoOutput.setNumero(ficha.getEndereco().getNumero());
                enderecoOutput.setLocalidade(ficha.getEndereco().getCidade());
                enderecoOutput.setUf(ficha.getEndereco().getUf());
                usuarioTokenOutput.setEndereco(enderecoOutput);
            }
        }
        
        // Volunteer info if applicable
        if (usuario.getTipo() == TipoUsuario.VOLUNTARIO && voluntarioRepository != null) {
            Optional<Voluntario> voluntarioOpt = voluntarioRepository.findByUsuario(usuario);
            if (voluntarioOpt.isPresent()) {
                Voluntario voluntario = voluntarioOpt.get();
                usuarioTokenOutput.setFuncao(voluntario.getFuncao());
            }
        }
        
        return usuarioTokenOutput;
    }    public static UsuarioListarOutput of(Usuario usuario) {
        UsuarioListarOutput output = new UsuarioListarOutput();
        // Basic user info
        output.setIdUsuario(usuario.getIdUsuario());
        output.setEmail(usuario.getEmail());
        output.setTipo(usuario.getTipo());
        output.setDataCadastro(usuario.getDataCadastro());
        output.setCriadoEm(usuario.getCriadoEm());
        output.setAtualizadoEm(usuario.getAtualizadoEm());
        output.setVersao(usuario.getVersao());

        // Ficha info
        Ficha ficha = usuario.getFicha();
        if (ficha != null) {
            output.setIdFicha(ficha.getIdFicha());
            output.setNome(ficha.getNome());
            output.setSobrenome(ficha.getSobrenome());
            output.setCpf(ficha.getCpf());
            output.setDataNascimento(ficha.getDtNascim());
            output.setRenda(ficha.getRenda() != null ? ficha.getRenda().doubleValue() : null);
            output.setGenero(ficha.getGenero());
            output.setProfissao(ficha.getProfissao());

            // Endereco info
            if (ficha.getEndereco() != null) {
                EnderecoOutput enderecoOutput = new EnderecoOutput();
                enderecoOutput.setCep(ficha.getEndereco().getCep());
                enderecoOutput.setLogradouro(ficha.getEndereco().getLogradouro());
                enderecoOutput.setComplemento(ficha.getEndereco().getComplemento());
                enderecoOutput.setBairro(ficha.getEndereco().getBairro());
                enderecoOutput.setNumero(ficha.getEndereco().getNumero());
                enderecoOutput.setLocalidade(ficha.getEndereco().getCidade());
                enderecoOutput.setUf(ficha.getEndereco().getUf());
                output.setEndereco(enderecoOutput);
            }
        }
        
        // Volunteer info if applicable
        if (usuario.getTipo() == TipoUsuario.VOLUNTARIO && voluntarioRepository != null) {
            Optional<Voluntario> voluntarioOpt = voluntarioRepository.findByUsuario(usuario);
            if (voluntarioOpt.isPresent()) {
                Voluntario voluntario = voluntarioOpt.get();
                output.setFuncao(voluntario.getFuncao());
            }
        }
        return output;
    }    public static VoluntarioInput of(VoluntarioInputSegundaFase voluntarioInputSegundaFase, Integer idUsuario) {
        VoluntarioInput voluntario = new VoluntarioInput();
        voluntario.setFkUsuario(idUsuario);
        voluntario.setFuncao(voluntarioInputSegundaFase.getFuncao());
        return voluntario;
    }

    public static UsuarioPrimeiraFaseOutput ofPrimeiraFase(Usuario usuario) {
        UsuarioPrimeiraFaseOutput output = new UsuarioPrimeiraFaseOutput();
        output.setNome(usuario.getFicha().getNome());
        output.setEmail(usuario.getEmail());
        output.setCpf(usuario.getFicha().getCpf());
        output.setDataNascimento(usuario.getFicha().getDtNascim());
        return output;
    }

    public static UsuarioDetalhesOutput ofDetalhes(Usuario usuario, Ficha ficha) {
        return new UsuarioDetalhesOutput(usuario, ficha);
    }
}
