package org.com.imaapi.service;

import lombok.RequiredArgsConstructor;
import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.usuario.*;
import org.com.imaapi.model.usuario.input.AssistenteSocialInput;
import org.com.imaapi.model.usuario.output.AssistenteSocialOutput;
import org.com.imaapi.repository.FichaRepository;
import org.com.imaapi.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssistenteSocialService {
    private final UsuarioRepository usuarioRepository;
    private final FichaRepository fichaRepository;
    private final PasswordEncoder passwordEncoder;
    private final EnderecoService enderecoService;

    @Transactional
    public AssistenteSocialOutput cadastrarAssistenteSocial(AssistenteSocialInput input) {
        // Criar a ficha
        Ficha ficha = new Ficha();
        ficha.setNome(input.getNome());
        ficha.setSobrenome(input.getSobrenome());
        ficha.setProfissao("Assistente Social");
        
        // Criar/atualizar endereço
        if (input.getEndereco() != null) {
            Endereco endereco = enderecoService.criarOuAtualizarEndereco(input.getEndereco());
            ficha.setEndereco(endereco);
        }
        
        fichaRepository.save(ficha);

        // Criar usuário
        Usuario usuario = new Usuario();
        usuario.setFicha(ficha);
        usuario.setEmail(input.getEmail());
        usuario.setSenha(passwordEncoder.encode(input.getSenha()));
        usuario.setTipo(TipoUsuario.ADMINISTRADOR);
        
        // Salvar usuário
        usuarioRepository.save(usuario);

        return converterParaOutput(usuario);
    }

    @Transactional
    public AssistenteSocialOutput atualizarAssistenteSocial(Integer idUsuario, AssistenteSocialInput input) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Assistente Social não encontrado"));

        Ficha ficha = usuario.getFicha();
        ficha.setNome(input.getNome());
        ficha.setSobrenome(input.getSobrenome());
        
        if (input.getEndereco() != null) {
            Endereco endereco = enderecoService.criarOuAtualizarEndereco(input.getEndereco());
            ficha.setEndereco(endereco);
        }
        
        fichaRepository.save(ficha);
        
        usuario.setEmail(input.getEmail());
        if (input.getSenha() != null && !input.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(input.getSenha()));
        }
        
        usuarioRepository.save(usuario);
        
        return converterParaOutput(usuario);
    }

    public AssistenteSocialOutput buscarAssistenteSocial(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Assistente Social não encontrado"));
                
        return converterParaOutput(usuario);
    }

    private AssistenteSocialOutput converterParaOutput(Usuario usuario) {
        AssistenteSocialOutput output = new AssistenteSocialOutput();
        output.setIdUsuario(usuario.getIdUsuario());
        output.setNome(usuario.getFicha().getNome());
        output.setSobrenome(usuario.getFicha().getSobrenome());
        output.setEmail(usuario.getEmail());
        output.setFotoUrl(usuario.getFotoUrl());
        output.setEndereco(usuario.getFicha().getEndereco());
        
        return output;
    }
}
