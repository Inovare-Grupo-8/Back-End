package org.com.imaapi.model.usuario;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.enums.converter.TipoUsuarioConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "usuario")
@Data
public class Usuario {    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;
    private Usuario usuario;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Ficha getFicha() {
        return ficha;
    }

    public void setFicha(Ficha ficha) {
        this.ficha = ficha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public LocalDateTime getUltimoAcesso() {
        return ultimoAcesso;
    }

    public void setUltimoAcesso(LocalDateTime ultimoAcesso) {
        this.ultimoAcesso = ultimoAcesso;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

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