package org.com.imaapi.model.usuario.output;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UsuarioListarOutput {
    private Integer id;
    private String nome;
    private String email;
}
