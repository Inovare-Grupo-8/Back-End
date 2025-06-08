package org.com.imaapi.model.usuario.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UsuarioInputAtualizacaoDadosPessoais {
    private String nome;

    private String sobrenome;

    private String telefone;

    @Email(message = "Email inválido")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$",
            message = "A senha deve ter pelo menos 6 caracteres, incluindo letras, números e um caractere especial")
    private String senha;    @PastOrPresent(message = "Data de nascimento inválida")
    private LocalDate dataNascimento;
    
    // Campos específicos para assistente social
    private String crp;
    private String bio;
    private String especialidade;
}
