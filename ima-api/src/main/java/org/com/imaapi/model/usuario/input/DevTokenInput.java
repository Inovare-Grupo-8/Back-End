package org.com.imaapi.model.usuario.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
public class DevTokenInput {
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotEmpty(message = "As authorities são obrigatórias")
    private List<String> authorities;

    private Long validityInSeconds;

    public @NotBlank(message = "O email é obrigatório") @Email(message = "Email inválido") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "O email é obrigatório") @Email(message = "Email inválido") String email) {
        this.email = email;
    }

    public @NotBlank(message = "O nome é obrigatório") String getNome() {
        return nome;
    }

    public void setNome(@NotBlank(message = "O nome é obrigatório") String nome) {
        this.nome = nome;
    }

    public @NotEmpty(message = "As authorities são obrigatórias") List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(@NotEmpty(message = "As authorities são obrigatórias") List<String> authorities) {
        this.authorities = authorities;
    }

    public Long getValidityInSeconds() {
        return validityInSeconds;
    }

    public void setValidityInSeconds(Long validityInSeconds) {
        this.validityInSeconds = validityInSeconds;
    }
}
