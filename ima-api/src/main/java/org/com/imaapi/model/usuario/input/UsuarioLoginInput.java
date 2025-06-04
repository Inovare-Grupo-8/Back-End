package org.com.imaapi.model.usuario.input;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioLoginInput {
    @NotBlank
    private String email;

    @NotBlank
    private String senha;
}
