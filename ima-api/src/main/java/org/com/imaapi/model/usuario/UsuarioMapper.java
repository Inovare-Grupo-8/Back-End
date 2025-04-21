package org.com.imaapi.model.usuario;

import org.com.imaapi.model.usuario.input.UsuarioAutenticacaoInput;
import org.com.imaapi.model.usuario.input.UsuarioInput;
import org.com.imaapi.model.usuario.input.VoluntarioInput;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;

public class UsuarioMapper {

    public static Usuario of(UsuarioInput usuarioInput) {
        Usuario usuario = new Usuario();

        usuario.setNome(usuarioInput.getNome());
        usuario.setEmail(usuarioInput.getEmail());
        usuario.setSenha(usuarioInput.getSenha());
        usuario.setCpf(usuarioInput.getCpf());
        usuario.setTipo(usuarioInput.getTipo());
        usuario.setGenero(usuarioInput.getGenero());
        usuario.setDataNascimento(usuarioInput.getDataNascimento());
        usuario.setRenda(usuarioInput.getRenda());

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

    public static VoluntarioInput of(UsuarioInput usuarioInput, Integer idUsuario) {
        VoluntarioInput voluntario = new VoluntarioInput();
        voluntario.setFkUsuario(idUsuario);
        voluntario.setFuncao(usuarioInput.getFuncao());
        return voluntario;
    }
}
