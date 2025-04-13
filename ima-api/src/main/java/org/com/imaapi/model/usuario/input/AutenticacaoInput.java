package org.com.imaapi.model.usuario.input;

import lombok.Data;

@Data
public class AutenticacaoInput {
    private String email;
    private String senha;
}
