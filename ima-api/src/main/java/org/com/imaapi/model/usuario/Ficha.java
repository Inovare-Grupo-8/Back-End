package org.com.imaapi.model.usuario;

import jakarta.persistence.*;
import org.com.imaapi.model.enums.Genero;
import org.com.imaapi.model.usuario.input.UsuarioInputSegundaFase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ficha")
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
    private String sobrenome;

    @Column(name = "cpf", unique = true, length = 11)
    private String cpf;

    @Column(name = "renda", precision = 10, scale = 2)
    private BigDecimal renda;

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
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

    // Getters
    public Integer getIdFicha() {
        return idFicha;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public String getNome() {
        return nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public String getCpf() {
        return cpf;
    }

    public BigDecimal getRenda() {
        return renda;
    }

    public Genero getGenero() {
        return genero;
    }

    public LocalDate getDtNascim() {
        return dtNascim;
    }

    public String getAreaOrientacao() {
        return areaOrientacao;
    }

    public String getComoSoube() {
        return comoSoube;
    }

    public String getProfissao() {
        return profissao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public Integer getVersao() {
        return versao;
    }

    // Setters
    public void setIdFicha(Integer idFicha) {
        this.idFicha = idFicha;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setRenda(BigDecimal renda) {
        this.renda = renda;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public void setDtNascim(LocalDate dtNascim) {
        this.dtNascim = dtNascim;
    }

    public void setAreaOrientacao(String areaOrientacao) {
        this.areaOrientacao = areaOrientacao;
    }

    public void setComoSoube(String comoSoube) {
        this.comoSoube = comoSoube;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    // Atualização dos dados da segunda fase
    public void atualizarDadosSegundaFase(UsuarioInputSegundaFase input) {
        this.setDtNascim(input.getDataNascimento());
        this.setRenda(input.getRenda() != null ? BigDecimal.valueOf(input.getRenda()) : null);
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