package org.com.imaapi.model.usuario.output;

import lombok.Data;
import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.usuario.Ficha;
import org.com.imaapi.model.usuario.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class UsuarioDetalhesOutput implements UserDetails {
    private static final long serialVersionUID = 3939036964410265319L;
    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioDetalhesOutput.class);

    private final String nome;
    private final String email;
    private final String senha;
    private final TipoUsuario tipo;
    private final Integer idUsuario;

    public UsuarioDetalhesOutput(Usuario usuario, Ficha ficha) {
//        LOGGER.debug("[USUARIO_DETALHES] Criando UsuarioDetalhesOutput para: {}", usuario.getEmail());

        String nomeUsuario;
        try {
            nomeUsuario = ficha != null ? ficha.getNome() : "N/A";
            LOGGER.debug("[USUARIO_DETALHES] Nome definido: {}", nomeUsuario);
        } catch (Exception e) {
            LOGGER.error("[USUARIO_DETALHES] Erro ao obter nome da ficha: {}", e.getMessage());
            nomeUsuario = "N/A";
        }
        this.nome = nomeUsuario;

        this.email = usuario.getEmail();
        this.senha = usuario.getSenha();
//        LOGGER.debug("[USUARIO_DETALHES] Email definido: {}, senha(hash) definida", this.email);

        this.tipo = usuario.getTipo();
//        LOGGER.debug("[USUARIO_DETALHES] Tipo de usuário definido: {}", this.tipo);
        
        this.idUsuario = usuario.getIdUsuario(); 
        LOGGER.debug("[USUARIO_DETALHES] ID do usuário definido: {}", this.idUsuario);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (tipo == null) {
//            LOGGER.warn("[USUARIO_DETALHES] Tipo de usuário nulo para: {}, retornando lista vazia de autoridades", email);
            return List.of();
        }
        String authority = "ROLE_" + tipo.name();
//        LOGGER.debug("[USUARIO_DETALHES] Autoridade para usuário {}: {}", email, authority);
        return List.of(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
//        LOGGER.debug("[USUARIO_DETALHES] Retornando senha (hash) para: {}", email);
        return senha;
    }

    @Override
    public String getUsername() {
//        LOGGER.debug("[USUARIO_DETALHES] Retornando username (email): {}", email);
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
