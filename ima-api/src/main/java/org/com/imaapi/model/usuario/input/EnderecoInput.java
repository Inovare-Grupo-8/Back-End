package org.com.imaapi.model.usuario.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EnderecoInput {
    @NotBlank(message = "O CEP não pode estar em branco")
    @Pattern(regexp = "\\d{8}|\\d{5}-\\d{3}", message = "O CEP deve estar no formato 00000000 ou 00000-000")
    private String cep;

    private String numero;

    private String complemento;
}