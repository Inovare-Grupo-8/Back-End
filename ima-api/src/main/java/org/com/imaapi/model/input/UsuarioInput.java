package org.com.imaapi.model.input;

import lombok.Data;
import org.com.imaapi.model.cargo.Funcao;

@Data
public class UsuarioInput {
    private String nome;
    private String email;
    private String senha;
    private Boolean isVoluntario;
    private Funcao funcao;
}
