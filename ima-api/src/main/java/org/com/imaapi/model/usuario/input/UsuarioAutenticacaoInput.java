package org.com.imaapi.model.usuario.input;

import lombok.Data;

@Data
public class UsuarioAutenticacaoInput {
    private String email;
    private String senha;
}
