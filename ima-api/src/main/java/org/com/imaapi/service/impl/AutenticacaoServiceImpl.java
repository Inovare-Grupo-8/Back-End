package org.com.imaapi.service.impl;

import org.com.imaapi.model.usuario.Ficha;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.output.UsuarioDetalhesOutput;
import org.com.imaapi.repository.FichaRepository;
import org.com.imaapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutenticacaoServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FichaRepository fichaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(username);

        if (usuarioOpt.isEmpty()) {
            throw new UsernameNotFoundException(String.format("Usuário %s não encontrado", username));
        }

        Usuario usuario = usuarioOpt.get();

        Ficha ficha = fichaRepository.findById(usuario.getFicha().getIdFicha())
                .orElseThrow(() -> new UsernameNotFoundException("Ficha não encontrada para o usuário."));

        return new UsuarioDetalhesOutput(usuario, ficha);
    }

}
