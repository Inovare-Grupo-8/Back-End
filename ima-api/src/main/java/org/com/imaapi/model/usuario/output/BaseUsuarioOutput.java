package org.com.imaapi.model.usuario.output;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public abstract class BaseUsuarioOutput {
    private Integer idUsuario;
    private String nome;
    private String email;
}
