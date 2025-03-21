package org.com.imaapi.model.Usuario.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.com.imaapi.model.Usuario.enums.Funcao;
import org.com.imaapi.model.Usuario.enums.Genero;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioOutput {
    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private String senha;
    private LocalDate dataNascimento;
    private Double renda;
    private Genero genero;
    private Funcao funcao;
    private LocalDateTime dataCadastro;
}