package org.com.imaapi.service;

import org.com.imaapi.model.usuario.Ficha;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.input.UsuarioInputPrimeiraFase;
import org.com.imaapi.model.usuario.input.UsuarioInputSegundaFase;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.model.usuario.output.UsuarioClassificacaoOutput;
import org.com.imaapi.model.usuario.output.VoluntarioListagemOutput;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Usuario cadastrarPrimeiraFase(UsuarioInputPrimeiraFase usuarioInputPrimeiraFase);
    
    Usuario cadastrarPrimeiraFaseVoluntario(UsuarioInputPrimeiraFase usuarioInputPrimeiraFase);    Usuario cadastrarSegundaFase(Integer idUsuario, UsuarioInputSegundaFase usuarioInputSegundaFase);
    
    Usuario cadastrarSegundaFaseVoluntario(Integer idUsuario, UsuarioInputSegundaFase usuarioInputSegundaFase);

    UsuarioTokenOutput autenticar(Usuario usuario, Ficha ficha, String senha);

    List<UsuarioListarOutput> buscarUsuarios();

    Optional<Usuario> buscaUsuario(Integer id);

    public Optional<Usuario> buscaUsuarioPorNome(String nome);

    void cadastrarUsuarioOAuth(OAuth2User usuario);

    Optional<Usuario> buscaUsuarioPorEmail(String email);

    UsuarioListarOutput atualizarUsuario(Integer id, UsuarioInputSegundaFase usuarioInputSegundaFase);

    void deletarUsuario(Integer id);
      
    Usuario buscarDadosPrimeiraFase(Integer idUsuario);    Usuario buscarDadosPrimeiraFase(String email);

    String enviarCredenciaisVoluntario(String email, String nome, String senha);

    List<UsuarioClassificacaoOutput> buscarUsuariosNaoClassificados();    UsuarioListarOutput classificarUsuarioComoGratuidade(Integer id);

    UsuarioListarOutput classificarUsuarioComoValorSocial(Integer id);
    
    void atualizarUltimoAcesso(Integer idUsuario);

    List<VoluntarioListagemOutput> listarVoluntarios();
}