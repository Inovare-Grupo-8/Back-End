package org.com.imaapi.model.usuario.output;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UsuarioPrimeiraFaseOutput {
    private String nome;
    private String email;
    private String cpf;
    private LocalDate dataNascimento;
}
