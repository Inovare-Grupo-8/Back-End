package org.com.imaapi.service.impl;

import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.input.UsuarioInput;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.model.usuario.output.UsuarioOutput;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.PerfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PerfilServiceImpl implements PerfilService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
//    public UsuarioOutput buscarUsuarioComEnderecoPorId(Integer usuarioId) {
//        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);
//        if (usuarioOptional.isEmpty()) {
//            return null;
//        }
//        Usuario usuario = usuarioOptional.get();
//        UsuarioOutput usuarioOutput = new UsuarioOutput(usuario);
//        usuarioOutput.setNome(usuario.getNome());
//        usuarioOutput.setCpf(usuario.getCpf());
//        usuarioOutput.setEmail(usuario.getEmail());
//        usuarioOutput.setSenha(usuario.getSenha());
//        usuarioOutput.setDataNascimento(usuario.getDataNascimento());
//        usuarioOutput.setRenda(usuario.getRenda());
//        usuarioOutput.setGenero(usuario.getGenero());
//        usuarioOutput.setTipo(usuario.getTipo());
//        usuarioOutput.setDataCadastro(usuario.getDataCadastro());
//        if (usuario.getEndereco() != null) {
//            EnderecoOutput enderecoOutput = new EnderecoOutput();
//            enderecoOutput.setCep(usuario.getEndereco().getCep());
//            enderecoOutput.setNumero(usuario.getEndereco().getNumero());
//            enderecoOutput.setComplemento(usuario.getEndereco().getComplemento());
//            usuarioOutput.setEndereco(enderecoOutput);
//        }
//        return usuarioOutput;
//    }

    public UsuarioOutput buscarUsuarioComEnderecoPorId(Integer usuarioId) {
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        if (usuario == null) {
            return null;
        }
        return converterParaUsuarioOutput(usuario);
    }

    @Override
    public boolean atualizarEnderecoPorUsuarioId(Integer usuarioId, String cep, String numero, String complemento) {
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        if (usuario == null || usuario.getEndereco() == null) {
            return false;
        }
        atualizarEndereco(usuario, cep, numero, complemento);
        usuarioRepository.save(usuario);
        return true;
    }

    @Override
    public UsuarioOutput atualizarDadosPessoaisPorId(Integer usuarioId, UsuarioInput usuarioInput) {
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        if (usuario == null) {
            return null;
        }
        atualizarDadosPessoais(usuario, usuarioInput);
        usuarioRepository.save(usuario);
        return converterParaUsuarioOutput(usuario);
    }

    // Método auxiliar para buscar usuário por ID
    private Usuario buscarUsuarioPorId(Integer usuarioId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);
        return usuarioOptional.orElse(null);
    }

    // Método auxiliar para atualizar endereço
    private void atualizarEndereco(Usuario usuario, String cep, String numero, String complemento) {
        usuario.getEndereco().setCep(cep);
        usuario.getEndereco().setNumero(numero);
        usuario.getEndereco().setComplemento(complemento);
    }

    // Método auxiliar para atualizar dados pessoais
    private void atualizarDadosPessoais(Usuario usuario, UsuarioInput usuarioInput) {
        usuario.setNome(usuarioInput.getNome());
        usuario.setCpf(usuarioInput.getCpf());
        usuario.setEmail(usuarioInput.getEmail());
        usuario.setSenha(usuarioInput.getSenha());
        usuario.setDataNascimento(usuarioInput.getDataNascimento());
        usuario.setRenda(usuarioInput.getRenda());
        usuario.setGenero(usuarioInput.getGenero());
        usuario.setTipo(usuarioInput.getTipo());

        if (usuario.getEndereco() != null) {
            atualizarEndereco(usuario, usuarioInput.getCep(), usuarioInput.getNumero(), usuarioInput.getComplemento());
        }
    }

    // Método auxiliar para converter Usuario para UsuarioOutput
    private UsuarioOutput converterParaUsuarioOutput(Usuario usuario) {
        return new UsuarioOutput(usuario);
    }
}