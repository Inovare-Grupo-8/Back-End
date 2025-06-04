package org.com.imaapi.model.usuario.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoInput {
    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve ter 8 dígitos")
    private String cep;

    @NotBlank(message = "Número é obrigatório")
    @Size(max = 10, message = "Número não pode ter mais que 10 caracteres")
    private String numero;

    @NotBlank(message = "Complemento é obrigatório")
    private String complemento;
}
