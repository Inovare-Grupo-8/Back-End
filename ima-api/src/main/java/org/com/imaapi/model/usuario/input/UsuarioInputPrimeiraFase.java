package org.com.imaapi.model.usuario.input;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

import org.com.imaapi.model.enums.Funcao;
import org.com.imaapi.model.enums.Genero;
import org.com.imaapi.model.enums.TipoUsuario;

@Data
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsuarioInputPrimeiraFase {
    @NotBlank(message = "Nome não pode estar em branco")
    @Size(min = 3, message = "O nome deve ter entre 3 e 50 caracteres")
    private String nome;

    @NotNull(message = "CPF não pode ser nulo")
    @Digits(integer = 11, fraction = 0, message = "CPF deve ter 11 digitos")
    private String cpf;

    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "O campo senha não pode estar em branco")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$",
            message = "A senha deve ter pelo menos 6 caracteres, incluindo letras, números e um caractere especial")
    private String senha;

    @PastOrPresent(message = "Data de nascimento inválida")
    private LocalDate dataNascimento;

}