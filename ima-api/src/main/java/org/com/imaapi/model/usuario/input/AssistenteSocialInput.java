package org.com.imaapi.model.usuario.input;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;
import lombok.Data;
import org.com.imaapi.model.enums.Funcao;
import org.com.imaapi.model.enums.TipoUsuario;

@Data
public class AssistenteSocialInput {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Sobrenome é obrigatório")
    private String sobrenome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    @NotBlank(message = "CPF é obrigatório")
    @CPF(message = "CPF inválido")
    private String cpf;

    @NotBlank(message = "Data de nascimento é obrigatória")
    private String dataNascimento;

    @NotBlank(message = "Gênero é obrigatório")
    private String genero;

    private Double renda;

    private TipoUsuario tipo = TipoUsuario.ADMINISTRADOR;
    private Funcao funcao = Funcao.ASSISTENCIA_SOCIAL;

    @NotBlank(message = "Profissão é obrigatória")
    private String profissao;

    @NotBlank(message = "DDD é obrigatório")
    private String ddd;

    @NotBlank(message = "Número é obrigatório")
    private String numero;

    @NotBlank(message = "CEP é obrigatório")
    private String cep;

    private String complemento;

    private String crp;

    private String especialidade;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    private String bio;

    public @NotBlank(message = "Nome é obrigatório") String getNome() {
        return nome;
    }

    public void setNome(@NotBlank(message = "Nome é obrigatório") String nome) {
        this.nome = nome;
    }

    public @NotBlank(message = "Sobrenome é obrigatório") String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(@NotBlank(message = "Sobrenome é obrigatório") String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Senha é obrigatória") String getSenha() {
        return senha;
    }

    public void setSenha(@NotBlank(message = "Senha é obrigatória") String senha) {
        this.senha = senha;
    }

    public @NotBlank(message = "CPF é obrigatório") @CPF(message = "CPF inválido") String getCpf() {
        return cpf;
    }

    public void setCpf(@NotBlank(message = "CPF é obrigatório") @CPF(message = "CPF inválido") String cpf) {
        this.cpf = cpf;
    }

    public @NotBlank(message = "Data de nascimento é obrigatória") String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(@NotBlank(message = "Data de nascimento é obrigatória") String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public @NotBlank(message = "Gênero é obrigatório") String getGenero() {
        return genero;
    }

    public void setGenero(@NotBlank(message = "Gênero é obrigatório") String genero) {
        this.genero = genero;
    }

    public Double getRenda() {
        return renda;
    }

    public void setRenda(Double renda) {
        this.renda = renda;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public @NotBlank(message = "Profissão é obrigatória") String getProfissao() {
        return profissao;
    }

    public void setProfissao(@NotBlank(message = "Profissão é obrigatória") String profissao) {
        this.profissao = profissao;
    }

    public @NotBlank(message = "DDD é obrigatório") String getDdd() {
        return ddd;
    }

    public void setDdd(@NotBlank(message = "DDD é obrigatório") String ddd) {
        this.ddd = ddd;
    }

    public @NotBlank(message = "Número é obrigatório") String getNumero() {
        return numero;
    }

    public void setNumero(@NotBlank(message = "Número é obrigatório") String numero) {
        this.numero = numero;
    }

    public @NotBlank(message = "CEP é obrigatório") String getCep() {
        return cep;
    }

    public void setCep(@NotBlank(message = "CEP é obrigatório") String cep) {
        this.cep = cep;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCrp() {
        return crp;
    }

    public void setCrp(String crp) {
        this.crp = crp;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public @NotBlank(message = "Telefone é obrigatório") String getTelefone() {
        return telefone;
    }

    public void setTelefone(@NotBlank(message = "Telefone é obrigatório") String telefone) {
        this.telefone = telefone;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
