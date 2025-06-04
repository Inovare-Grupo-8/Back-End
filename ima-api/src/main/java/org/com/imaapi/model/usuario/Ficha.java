package org.com.imaapi.model.usuario;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.com.imaapi.model.enums.Genero;
import org.com.imaapi.model.usuario.input.UsuarioInputSegundaFase;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ficha")
@NoArgsConstructor
@AllArgsConstructor
public class Ficha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ficha")
    private Integer idFicha;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "sobrenome", nullable = false)
    private String sobrenome;

    @Column(name = "cpf", unique = true)
    private String cpf;

    @Column(name = "renda")
    private Double renda;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero")
    private Genero genero;

    @Column(name = "dt_nascim")
    private LocalDate dtNascim;

    @ManyToOne
    @JoinColumn(name = "fk_endereco")
    private Endereco endereco;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Integer version;    
    
    @Column(name = "area_orientacao")
    private String areaOrientacao;

    @Column(name = "como_soube")
    private String comoSoube;
    
    @Column(name = "profissao")
    private String profissao;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.version == null) {
            this.version = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void atualizarDadosSegundaFase(UsuarioInputSegundaFase input) {
        this.setCpf(input.getCpf());
        this.setDtNascim(input.getDataNascimento());
        this.setRenda(input.getRenda());

        try {
            Genero generoEnum = Genero.valueOf(input.getGenero().toUpperCase());
            this.setGenero(generoEnum);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Gênero inválido: " + input.getGenero());
        }
    }
}
