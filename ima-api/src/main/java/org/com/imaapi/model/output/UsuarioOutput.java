package org.com.imaapi.model.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.com.imaapi.model.cargo.Funcao;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioOutput {
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private Funcao funcao;
}