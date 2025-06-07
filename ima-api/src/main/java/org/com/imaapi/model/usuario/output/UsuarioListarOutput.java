package org.com.imaapi.model.usuario.output;

import lombok.Data;
import org.com.imaapi.model.enums.TipoUsuario;

@Data
public class UsuarioListarOutput extends BaseUsuarioOutput {
    private TipoUsuario tipo;
}
