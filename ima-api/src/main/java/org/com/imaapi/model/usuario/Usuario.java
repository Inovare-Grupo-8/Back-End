package org.com.imaapi.model.usuario;

import jakarta.persistence.*;
import lombok.*;
import org.com.imaapi.model.enums.Genero;
import org.com.imaapi.model.enums.TipoUsuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id_usuario")
    private Integer idUsuario;

    @Column (name = "nome")
    private String nome;

    @Column (name = "cpf")
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoUsuario tipo;

    @Column (name = "email")
    private String email;

    @Column (name = "senha")
    private String senha;

    @Column (name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column (name = "renda")
    private Double renda;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero", length = 1)
    private Genero genero;

    @Column (name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @Setter
    @ManyToOne
    @JoinColumn(name = "fk_endereco")
    private Endereco endereco;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
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