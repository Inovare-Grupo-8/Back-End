package org.com.imaapi.model.usuario.input;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelefoneInput {
    @Pattern(regexp = "\\d{2}", message = "DDD deve ter 2 dígitos")
    private String ddd;

    @Pattern(regexp = "\\d{5}", message = "Prefixo deve ter 5 dígitos")
    private String prefixo;

    @Pattern(regexp = "\\d{4}", message = "Sufixo deve ter 4 dígitos")
    private String sufixo;

    private Boolean whatsapp;
}
