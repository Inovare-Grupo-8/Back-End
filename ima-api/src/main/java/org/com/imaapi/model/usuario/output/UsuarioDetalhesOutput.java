package org.com.imaapi.model.usuario.output;

import lombok.Data;
import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.usuario.Ficha;
import org.com.imaapi.model.usuario.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class UsuarioDetalhesOutput implements UserDetails {
    private final String nome;
    private final String email;
    private final String senha;
    private final TipoUsuario tipo;

    public UsuarioDetalhesOutput(Usuario usuario, Ficha ficha) {
        this.nome = ficha.getNome();
        this.email = usuario.getEmail();
        this.senha = usuario.getSenha();
        this.tipo = usuario.getTipo();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (tipo == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + tipo.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
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
