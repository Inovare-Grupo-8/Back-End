package org.com.imaapi.model.usuario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.com.imaapi.model.enums.Genero;
import org.com.imaapi.model.usuario.input.UsuarioInputSegundaFase;

import java.math.BigDecimal;
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

    @ManyToOne
    @JoinColumn(name = "fk_endereco")
    private Endereco endereco;

    @Column(name = "nome", nullable = false, length = 45)
    private String nome;

    @Column(name = "sobrenome", nullable = false, length = 45)
    private String sobrenome;    @Column(name = "cpf", unique = true, length = 11)
    private String cpf;

    @Column(name = "renda_minima", precision = 10, scale = 2)
    private BigDecimal rendaMinima;

    @Column(name = "renda_maxima", precision = 10, scale = 2)
    private BigDecimal rendaMaxima;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero", length = 20)

    private Genero genero;

    @Column(name = "dt_nascim")
    private LocalDate dtNascim;

    @Column(name = "area_orientacao", length = 255)
    private String areaOrientacao;

    @Column(name = "como_soube", length = 255)
    private String comoSoube;

    @Column(name = "profissao", length = 255)
    private String profissao;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Version
    @Column(name = "versao")
    private Integer versao;

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
    }    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }      public void atualizarDadosSegundaFase(UsuarioInputSegundaFase input) {
        this.setDtNascim(input.getDataNascimento());
        
        // Trabalhar apenas com intervalos de renda (rendaMinima e rendaMaxima)
        if (input.getRendaMinima() != null && input.getRendaMaxima() != null) {
            this.setRendaMinima(BigDecimal.valueOf(input.getRendaMinima()));
            this.setRendaMaxima(BigDecimal.valueOf(input.getRendaMaxima()));
        } else {
            // Para casos de "prefiro não informar" ou sem informação
            this.setRendaMinima(null);
            this.setRendaMaxima(null);
        }
        
        this.setAreaOrientacao(input.getAreaOrientacao());
        this.setComoSoube(input.getComoSoube());
        this.setProfissao(input.getProfissao());

        try {
            String generoInput = input.getGenero().trim().toUpperCase();
            switch (generoInput) {
                case "M":
                case "MASCULINO":
                    this.setGenero(Genero.MASCULINO);
                    break;
                case "F":
                case "FEMININO":
                    this.setGenero(Genero.FEMININO);
                    break;
                case "O":
                case "OUTRO":
                    this.setGenero(Genero.OUTRO);
                    break;
                default:
                    throw new IllegalArgumentException("Gênero inválido: " + input.getGenero());
            }
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Gênero não pode ser nulo");
        }
    }
}
