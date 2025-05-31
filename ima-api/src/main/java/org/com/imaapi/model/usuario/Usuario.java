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
@Getter
@Setter
public class Usuario{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id_usuario")
    private Integer idUsuario;

    @Column (name = "nome")
    private String nome;

    @Column (name = "cpf", unique = true)
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoUsuario tipo;

    @Column (name = "email", unique = true)
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

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "fk_endereco", referencedColumnName = "id_endereco")
    private Endereco endereco;

    //para gerar a data de cadastro no banco
    @PrePersist
    public void prePersist() {
        if (this.dataCadastro == null) {
            this.dataCadastro = LocalDateTime.now();
        }
    }
}