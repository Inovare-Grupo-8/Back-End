package org.com.imaapi.model.Usuario.input;

import lombok.Data;

import java.time.LocalDate;

import org.com.imaapi.model.Usuario.enums.Funcao;
import org.com.imaapi.model.Usuario.enums.Genero;

@Data
public class UsuarioInput {
    private String nome;
    private String cpf;
    private String email;
    private String senha;
    private LocalDate dataNascimento;
    private Double renda;
    private Genero genero;
    private Boolean isVoluntario;
    private Funcao funcao;
}
