package org.com.imaapi.service;

import org.com.imaapi.model.usuario.input.UsuarioInput;
import org.com.imaapi.model.usuario.output.UsuarioOutput;

public interface PerfilService {
    UsuarioOutput buscarUsuarioComEnderecoPorId(Integer usuarioId);
    boolean atualizarEnderecoPorUsuarioId(Integer usuarioId, String cep, String numero, String complemento);
    UsuarioOutput atualizarDadosPessoaisPorId(Integer usuarioId, UsuarioInput usuarioInput);
}