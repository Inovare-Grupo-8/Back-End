package org.com.imaapi.model.usuario;

import jakarta.persistence.*;
import lombok.Data;
import org.com.imaapi.model.usuario.input.TelefoneInput;

import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "telefone")
public class Telefone {    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_telefone")
    private Integer idTelefone;

    @ManyToOne
    @JoinColumn(name = "fk_ficha", nullable = false)
    private Ficha ficha;

    @Column(name = "ddd", length = 2)
    private String ddd;

    @Column(name = "prefixo", length = 5)
    private String prefixo;

    @Column(name = "sufixo", length = 4)
    private String sufixo;

    @Column(name = "whatsapp")
    private Boolean whatsapp;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Version
    @Column(name = "versao")
    private Integer versao = 0;

    @PrePersist
    public void prePersist() {
        if (this.criadoEm == null) {
            this.criadoEm = LocalDateTime.now();
        }
        if (this.atualizadoEm == null) {
            this.atualizadoEm = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

    public static Telefone of(TelefoneInput telefoneInput, Ficha ficha) {
        Telefone telefone = new Telefone();
        telefone.setFicha(ficha);
        telefone.setDdd(telefoneInput.getDdd());
        telefone.setPrefixo(telefoneInput.getPrefixo());
        telefone.setSufixo(telefoneInput.getSufixo());
        telefone.setWhatsapp(telefoneInput.getWhatsapp());
        return telefone;
    }

    public Integer getIdTelefone() {
        return idTelefone;
    }

    public void setIdTelefone(Integer idTelefone) {
        this.idTelefone = idTelefone;
    }


    public Ficha getFicha() {
        return ficha;
    }

    public void setFicha(Ficha ficha) {
        this.ficha = ficha;
    }

    public String getDdd() {
        return ddd;
    }

    public void setDdd(String ddd) {
        this.ddd = ddd;
    }

    public String getPrefixo() {
        return prefixo;
    }

    public void setPrefixo(String prefixo) {
        this.prefixo = prefixo;
    }

    public String getSufixo() {
        return sufixo;
    }

    public void setSufixo(String sufixo) {
        this.sufixo = sufixo;
    }

    public Boolean getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(Boolean whatsapp) {
        this.whatsapp = whatsapp;
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

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }
}