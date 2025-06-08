package org.com.imaapi.model.usuario.output;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UsuarioDadosPessoaisOutput {
    private String nome;
    private String sobrenome;
    private String cpf;
    private String telefone;
    private String email;
    private LocalDate dataNascimento;
    private String tipo;
}