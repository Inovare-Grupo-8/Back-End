package org.com.imaapi.model.usuario;

import jakarta.persistence.*;
import lombok.*;
import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.enums.converter.TipoUsuarioConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuario")
@Getter
@Setter
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;
      
    @OneToOne
    @JoinColumn(name = "fk_ficha", unique = true, nullable = false, 
        foreignKey = @ForeignKey(name = "fk_usuario_ficha"))
    private Ficha ficha;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senha;    
    
    @Column(name = "tipo")
    @Convert(converter = TipoUsuarioConverter.class)
    private TipoUsuario tipo = TipoUsuario.NAO_CLASSIFICADO;

    @Column(name = "dt_cadastro", nullable = false)
    private LocalDate dataCadastro;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Integer version;

    @PrePersist
    public void prePersist() {
        if (this.dataCadastro == null) {
            this.dataCadastro = LocalDate.now();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.tipo == null) {
            this.tipo = TipoUsuario.NAO_CLASSIFICADO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }    
    
    public static Usuario criarUsuarioBasico(String email, String senha, Ficha ficha) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setTipo(TipoUsuario.NAO_CLASSIFICADO);
        usuario.setDataCadastro(LocalDate.now());
        usuario.setCreatedAt(LocalDateTime.now());
        usuario.setUpdatedAt(LocalDateTime.now());
        usuario.setVersion(0);
        usuario.setFicha(ficha);
        return usuario;
    }

    public void atualizarTipo(TipoUsuario tipo) {
        this.setTipo(tipo);
    }
}