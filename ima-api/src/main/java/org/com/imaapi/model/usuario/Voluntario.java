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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_voluntario")
    private Integer idVoluntario;

    @Enumerated(EnumType.STRING)
    @Column(name = "funcao")
    private Funcao funcao;

    @Column(name = "dt_cadastro", nullable = false)
    private LocalDate dataCadastro;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "registro_profissional")
    private String registroProfissional;

    @Column(name = "biografia_profissional", length = 500)
    private String biografiaProfissional;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Version
    @Column(name = "versao")
    private Integer versao;

    @Setter
    @ManyToOne
    @JoinColumn(name = "fk_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;

    @OneToMany(mappedBy = "voluntario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Disponibilidade> disponibilidades;

    @PrePersist
    public void prePersist() {
        if (this.criadoEm == null) {
            this.criadoEm = LocalDateTime.now();
        }
        if (this.atualizadoEm == null) {
            this.atualizadoEm = LocalDateTime.now();
        }
        if (this.versao == null) {
            this.versao = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}