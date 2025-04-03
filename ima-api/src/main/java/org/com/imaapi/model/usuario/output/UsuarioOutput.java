package org.com.imaapi.model.usuario.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.com.imaapi.model.enums.Funcao;
import org.com.imaapi.model.enums.Genero;

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