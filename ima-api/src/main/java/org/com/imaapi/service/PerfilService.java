package org.com.imaapi.service;

import org.com.imaapi.model.usuario.input.UsuarioInputAtualizacaoDadosPessoais;
import org.com.imaapi.model.usuario.input.UsuarioInputPrimeiraFase;
import org.com.imaapi.model.usuario.input.VoluntarioDadosProfissionaisInput;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.model.usuario.output.UsuarioDadosPessoaisOutput;
import org.com.imaapi.model.usuario.output.UsuarioOutput;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface PerfilService {
    // Métodos genéricos para buscar e atualizar dados
    UsuarioDadosPessoaisOutput buscarDadosPessoaisPorId(Integer usuarioId);
    EnderecoOutput buscarEnderecoPorId(Integer usuarioId);
    UsuarioOutput atualizarDadosPessoais(Integer usuarioId, UsuarioInputAtualizacaoDadosPessoais usuarioInputAtualizacaoDadosPessoais);
    UsuarioDadosPessoaisOutput atualizarDadosPessoaisCompleto(Integer usuarioId, UsuarioInputAtualizacaoDadosPessoais usuarioInputAtualizacaoDadosPessoais);
    boolean atualizarEnderecoPorUsuarioId(Integer usuarioId, String cep, String numero, String complemento);
    boolean atualizarDadosProfissionais(Integer usuarioId, VoluntarioDadosProfissionaisInput dadosProfissionais);
    boolean criarDisponibilidade(Integer usuarioId, Map<String, Object> disponibilidade);
    boolean atualizarDisponibilidade(Integer usuarioId, Map<String, Object> disponibilidade);
    String salvarFoto(Integer usuarioId, String tipo, MultipartFile file) throws IOException;

}