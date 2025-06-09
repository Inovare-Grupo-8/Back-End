package org.com.imaapi.model.usuario.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.com.imaapi.model.enums.Funcao;
import org.com.imaapi.model.enums.TipoUsuario;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsuarioInputSegundaFase {
    @NotNull(message = "Data de nascimento não pode ser nula")
    @PastOrPresent(message = "Data de nascimento inválida")
    private LocalDate dataNascimento;

    private Double renda;

    @NotNull(message = "O gênero deve ser informado")
    private String genero;

    @NotNull(message = "O tipo de usuário deve ser informado")
    private TipoUsuario tipo;

    @NotNull(message = "Informações do endereço são obrigatórias")
    private EnderecoInput endereco;

    @NotNull(message = "Informações do telefone são obrigatórias")
    private TelefoneInput telefone;

    private Funcao funcao;

    private String areaOrientacao;

    private String comoSoube;

    private String profissao;

    // Getters
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public Double getRenda() {
        return renda;
    }

    public String getGenero() {
        return genero;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public EnderecoInput getEndereco() {
        return endereco;
    }

    public TelefoneInput getTelefone() {
        return telefone;
    }

    public Funcao getFuncao() {
        return funcao;
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

    // Setters
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setRenda(Double renda) {
        this.renda = renda;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public void setEndereco(EnderecoInput endereco) {
        this.endereco = endereco;
    }

    public void setTelefone(TelefoneInput telefone) {
        this.telefone = telefone;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
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
}