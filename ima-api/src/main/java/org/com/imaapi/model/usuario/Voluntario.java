package org.com.imaapi.model.usuario;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;
import org.com.imaapi.model.enums.Funcao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "voluntario")
public class Voluntario {
    @Id
    @Column(name = "id_voluntario")
    private Integer idVoluntario;

    @Column(name = "funcao", nullable = false, length = 45)
    private String funcao;

    @Column(name = "dt_cadastro", nullable = false)
    private LocalDate dataCadastro;

    @Column(name = "biografia_profissional")
    private String biografiaProfissional;

    @Column(name = "registro_profissional", length = 55)
    private String registroProfissional;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Version
    @Column(name = "versao")
    private Integer versao;

    @Setter
    @ManyToOne
    @JoinColumn(name = "fk_usuario", referencedColumnName = "id_usuario", insertable = false, updatable = false)
    private Usuario usuario;

    @Column(name = "fk_usuario", unique = true, nullable = false)
    private Integer fkUsuario;

    @OneToMany(mappedBy = "voluntario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Disponibilidade> disponibilidades;

    public void setFuncao(Funcao funcao) {
        if (funcao != null) {
            // Store the normalized value to ensure consistency
            this.funcao = Funcao.normalizeValue(funcao.getValue());
        } else {
            this.funcao = null;
        }
    }

    public Funcao getFuncao() {
        try {
            return this.funcao != null ? Funcao.fromValue(this.funcao) : null;
        } catch (IllegalArgumentException e) {
            // Log the error but don't throw it to prevent data loading issues
            System.err.println("Warning: Invalid funcao value in database: " + this.funcao);
            return null;
        }
    }

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        this.versao = 0;
        this.idVoluntario = this.fkUsuario;
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}