package org.com.imaapi.model.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.com.imaapi.model.enums.Funcao;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioOutput {
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String senha;
    private LocalDate dataNascimento;
    private Double renda;
    private String genero;
    private Funcao funcao;
    private LocalDateTime dataCadastro;
}