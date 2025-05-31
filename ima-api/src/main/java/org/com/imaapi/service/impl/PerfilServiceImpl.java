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
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);
        if (usuarioOptional.isEmpty()) {
            return null;
        }
        return new UsuarioOutput(usuarioOptional.get());
    }

    @Override
    public boolean atualizarEnderecoPorUsuarioId(Integer usuarioId, String cep, String numero, String complemento) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);
        if (usuarioOptional.isEmpty()) {
            return false;
        }
        Usuario usuario = usuarioOptional.get();
        usuario.getEndereco().setCep(cep);
        usuario.getEndereco().setNumero(numero);
        usuario.getEndereco().setComplemento(complemento);
        usuarioRepository.save(usuario);
        return true;
    }

    @Override
    public UsuarioOutput atualizarDadosPessoaisPorId(Integer usuarioId, UsuarioInput usuarioInput) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);
        if (usuarioOptional.isEmpty()) {
            return null;
        }
        Usuario usuario = usuarioOptional.get();
        usuario.setNome(usuarioInput.getNome());
        usuario.setCpf(usuarioInput.getCpf());
        usuario.setEmail(usuarioInput.getEmail());
        usuario.setSenha(usuarioInput.getSenha());
        usuario.setDataNascimento(usuarioInput.getDataNascimento());
        usuario.setRenda(usuarioInput.getRenda());
        usuario.setGenero(usuarioInput.getGenero());
        usuario.setTipo(usuarioInput.getTipo());

        // Atualizando o endereço
        if (usuario.getEndereco() != null) {
            usuario.getEndereco().setCep(usuarioInput.getCep());
            usuario.getEndereco().setNumero(usuarioInput.getNumero());
            usuario.getEndereco().setComplemento(usuarioInput.getComplemento());
        }

        usuarioRepository.save(usuario);
        return new UsuarioOutput(usuario);
    }
}