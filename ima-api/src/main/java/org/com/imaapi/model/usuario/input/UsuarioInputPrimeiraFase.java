package org.com.imaapi.model.usuario.input;

import jakarta.validation.constraints.*;

public class UsuarioInputPrimeiraFase {
    @NotBlank(message = "Nome não pode estar em branco")
    @Size(min = 3, message = "O nome deve ter entre 3 e 50 caracteres")
    private String nome;

    @NotBlank(message = "Nome não pode estar em branco")
    @Size(min = 3, message = "O nome deve ter entre 3 e 50 caracteres")
    private String sobrenome;

    @NotNull(message = "Email não pode ser nulo")
    @Email(message = "Email inválido")
    private String email;

    @NotNull(message = "CPF não pode ser nulo")
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos")
    private String cpf;

    @NotBlank(message = "O campo senha não pode estar em branco")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$",
            message = "A senha deve ter pelo menos 6 caracteres, incluindo letras, números e um caractere especial")
    private String senha;

    // Getters
    public String getNome() {
        return nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public String getSenha() {
        return senha;
    }

    // Setters
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}