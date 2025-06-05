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
}
