package org.com.imaapi.model.usuario.output;

import lombok.Data;
import org.com.imaapi.model.enums.Genero;

import java.time.LocalDate;

@Data
public class UsuarioDadosPessoaisOutput {
    private String nome;
    private String sobrenome;
    private String cpf;
    private String telefone;
    private String email;
    private LocalDate dataNascimento;
    private Genero genero;
    private String tipo;    // Campos específicos para assistente social
    private String crp;
    private String bio;
    private String especialidade;
    private String fotoUrl;
}