package org.com.imaapi.model.usuario.output;

import lombok.Data;
import org.com.imaapi.model.enums.TipoUsuario;

@Data
public class UsuarioTokenOutput extends BaseUsuarioOutput {
    private String token;
    private TipoUsuario tipo;
}
