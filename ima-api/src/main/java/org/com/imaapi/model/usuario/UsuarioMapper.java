package org.com.imaapi.model.usuario;

import org.com.imaapi.model.usuario.input.UsuarioAutenticacaoInput;
import org.com.imaapi.model.usuario.input.UsuarioInputPrimeiraFase;
import org.com.imaapi.model.usuario.input.UsuarioInputSegundaFase;
import org.com.imaapi.model.usuario.input.VoluntarioInput;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.model.usuario.output.UsuarioPrimeiraFaseOutput;

public class UsuarioMapper {

    public static Usuario of(UsuarioInputPrimeiraFase usuarioInputPrimeiraFase, UsuarioInputSegundaFase usuarioInputSegundaFase) {
        Usuario usuario = new Usuario();

        usuario.setNome(usuarioInputPrimeiraFase.getNome());
        usuario.setEmail(usuarioInputPrimeiraFase.getEmail());
        usuario.setSenha(usuarioInputPrimeiraFase.getSenha());
        usuario.setCpf(usuarioInputPrimeiraFase.getCpf());
        usuario.setTipo(usuarioInputSegundaFase.getTipo());
        usuario.setGenero(usuarioInputSegundaFase.getGenero());
        usuario.setDataNascimento(usuarioInputPrimeiraFase.getDataNascimento());
        usuario.setRenda(usuarioInputSegundaFase.getRenda());

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

        usuarioTokenOutput.setId(usuario.getIdUsuario());
        usuarioTokenOutput.setNome(usuario.getNome());
        usuarioTokenOutput.setEmail(usuario.getEmail());
        usuarioTokenOutput.setToken(token);

        return usuarioTokenOutput;
    }

    public static UsuarioListarOutput of(Usuario usuario) {
        UsuarioListarOutput usuarioListarOutput = new UsuarioListarOutput();

        usuarioListarOutput.setId(usuario.getIdUsuario());
        usuarioListarOutput.setNome(usuario.getNome());
        usuarioListarOutput.setEmail(usuario.getEmail());

        return usuarioListarOutput;
    }

    public static VoluntarioInput of(UsuarioInputSegundaFase usuarioInputSegundaFase, Integer idUsuario) {
        VoluntarioInput voluntario = new VoluntarioInput();
        voluntario.setFkUsuario(idUsuario);
        voluntario.setFuncao(usuarioInputSegundaFase.getFuncao());
        return voluntario;
    }

    public static UsuarioPrimeiraFaseOutput ofPrimeiraFase(Usuario usuario) {
        UsuarioPrimeiraFaseOutput output = new UsuarioPrimeiraFaseOutput();
        output.setNome(usuario.getNome());
        output.setEmail(usuario.getEmail());
        output.setCpf(usuario.getCpf());
        output.setDataNascimento(usuario.getDataNascimento());
        return output;
    }
}
