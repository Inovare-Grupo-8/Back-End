package org.com.imaapi.model.usuario.input;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class UsuarioAutenticacaoInput {
    private String email;
    private String senha;
}
