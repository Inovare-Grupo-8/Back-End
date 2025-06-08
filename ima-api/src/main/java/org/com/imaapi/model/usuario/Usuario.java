package org.com.imaapi.model.usuario;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.enums.converter.TipoUsuarioConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Data
public class Usuario {    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @OneToOne(optional = false)
    @JoinColumn(name = "fk_ficha", unique = true, nullable = false)
    private Ficha ficha;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "senha", nullable = false, length = 128)
    private String senha;

    @Column(name = "tipo", length = 20)
    @Convert(converter = TipoUsuarioConverter.class)
    private TipoUsuario tipo;

    @Column(name = "dt_cadastro", nullable = false)
    private LocalDate dataCadastro;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso;

    @Version
    @Column(name = "versao")
    private Integer versao;

    @Column(name = "foto_url")
    private String fotoUrl;

    //para gerar a data de cadastro no banco
    @PrePersist
    public void prePersist() {
        if (this.dataCadastro == null) {
            this.dataCadastro = LocalDate.now();
        }
        if (this.criadoEm == null) {
            this.criadoEm = LocalDateTime.now();
        }
        if (this.atualizadoEm == null) {
            this.atualizadoEm = LocalDateTime.now();
        }
        if (this.versao == null) {
            this.versao = 0;
        }
        if (this.tipo == null) {
            this.tipo = TipoUsuario.NAO_CLASSIFICADO;
        }
    }    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
      public boolean isVoluntario() {
        return this.tipo == TipoUsuario.VOLUNTARIO;
    }
    
    public static Usuario criarUsuarioBasico(String email, String senha, Ficha ficha) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setTipo(TipoUsuario.NAO_CLASSIFICADO);
        usuario.setDataCadastro(LocalDate.now());
        usuario.setCriadoEm(LocalDateTime.now());
        usuario.setAtualizadoEm(LocalDateTime.now());
        usuario.setUltimoAcesso(LocalDateTime.now());
        usuario.setVersao(0);
        usuario.setFicha(ficha);
        return usuario;
    }

    public static Usuario criarVoluntario(String email, String senha, Ficha ficha) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setTipo(TipoUsuario.VOLUNTARIO);
        usuario.setDataCadastro(LocalDate.now());
        usuario.setCriadoEm(LocalDateTime.now());
        usuario.setAtualizadoEm(LocalDateTime.now());
        usuario.setUltimoAcesso(LocalDateTime.now());
        usuario.setVersao(0);
        usuario.setFicha(ficha);
        return usuario;
    }

    public void atualizarTipo(TipoUsuario tipo) {
        this.setTipo(tipo);
    }
}