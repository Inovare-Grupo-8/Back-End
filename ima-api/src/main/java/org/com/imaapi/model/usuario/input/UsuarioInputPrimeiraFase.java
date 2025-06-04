package org.com.imaapi.model.usuario.input;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

    @NotBlank(message = "O campo senha não pode estar em branco")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$",
            message = "A senha deve ter pelo menos 6 caracteres, incluindo letras, números e um caractere especial")
    private String senha;
}