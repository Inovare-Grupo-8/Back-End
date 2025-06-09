package org.com.imaapi.model.usuario.output;

import lombok.Data;
import org.com.imaapi.model.enums.TipoUsuario;

@Data
public class UsuarioTokenOutput extends BaseUsuarioOutput {
    private String token;
    private TipoUsuario tipo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }
}
