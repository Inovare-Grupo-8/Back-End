package org.com.imaapi.service;

import org.com.imaapi.model.usuario.input.UsuarioInputAtualizacaoDadosPessoais;
import org.com.imaapi.model.usuario.input.UsuarioInputPrimeiraFase;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.model.usuario.output.UsuarioDadosPessoaisOutput;
import org.com.imaapi.model.usuario.output.UsuarioOutput;

public interface PerfilService {
    // Métodos genéricos para buscar e atualizar dados
    UsuarioDadosPessoaisOutput buscarDadosPessoaisPorId(Integer usuarioId);
    EnderecoOutput buscarEnderecoPorId(Integer usuarioId);
    UsuarioOutput atualizarDadosPessoais(Integer usuarioId, UsuarioInputAtualizacaoDadosPessoais usuarioInputAtualizacaoDadosPessoais);    boolean atualizarEnderecoPorUsuarioId(Integer usuarioId, String cep, String numero, String complemento);

    }