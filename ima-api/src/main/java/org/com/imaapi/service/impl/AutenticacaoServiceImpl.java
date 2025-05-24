/*package org.com.imaapi.service.impl;

import org.com.imaapi.model.usuario.Usuario;
//import org.com.imaapi.model.usuario.output.UsuarioDetalhesOutput;
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

  /*  @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(username);

        if (usuarioOpt.isEmpty()) {
            throw new UsernameNotFoundException(String.format("usuario %s não encontrado", username));
        }

        return new UsuarioDetalhesOutput(usuarioOpt.get());
    }*/

