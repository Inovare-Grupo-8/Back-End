package org.com.imaapi.service;

import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.input.UsuarioInput;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    public void cadastrarUsuario(UsuarioInput usuarioInput);

    public void cadastrarVoluntario(UsuarioInput usuarioInput);

    public UsuarioTokenOutput autenticar(Usuario usuario);

    public List<UsuarioListarOutput> buscarUsuarios();

    public Optional<Usuario> buscaUsuario(Integer id);

    public Optional<Usuario> buscaUsuarioPorNome(String nome);

    public UsuarioListarOutput atualizarUsuario(Integer id, UsuarioInput usuarioInput);

    public void deletarUsuario(Integer id);
}