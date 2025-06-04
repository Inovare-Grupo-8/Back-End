package org.com.imaapi.model.usuario;

import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.usuario.input.UsuarioAutenticacaoInput;
import org.com.imaapi.model.usuario.input.UsuarioInputPrimeiraFase;
import org.com.imaapi.model.usuario.input.UsuarioInputSegundaFase;
import org.com.imaapi.model.usuario.input.VoluntarioInput;
import org.com.imaapi.model.usuario.output.UsuarioDetalhesOutput;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.model.usuario.output.UsuarioPrimeiraFaseOutput;

public class UsuarioMapper {

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
