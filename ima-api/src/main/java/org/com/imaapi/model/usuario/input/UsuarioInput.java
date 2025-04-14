package org.com.imaapi.model.usuario.input;

import lombok.Data;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import org.com.imaapi.model.enums.Funcao;
import org.com.imaapi.model.enums.Genero;
@Getter @Setter
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
    private Integer enderecoId;
}
