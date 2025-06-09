package org.com.imaapi.model.usuario.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoInput {
    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve ter 8 dígitos")
    private String cep;

    @NotBlank(message = "Número é obrigatório")
    @Size(max = 10, message = "Número não pode ter mais que 10 caracteres")
    private String numero;

    @NotBlank(message = "Complemento é obrigatório")
    private String complemento;

    public @NotBlank(message = "CEP é obrigatório") @Pattern(regexp = "\\d{8}", message = "CEP deve ter 8 dígitos") String getCep() {
        return cep;
    }

    public void setCep(@NotBlank(message = "CEP é obrigatório") @Pattern(regexp = "\\d{8}", message = "CEP deve ter 8 dígitos") String cep) {
        this.cep = cep;
    }

    public @NotBlank(message = "Número é obrigatório") @Size(max = 10, message = "Número não pode ter mais que 10 caracteres") String getNumero() {
        return numero;
    }

    public void setNumero(@NotBlank(message = "Número é obrigatório") @Size(max = 10, message = "Número não pode ter mais que 10 caracteres") String numero) {
        this.numero = numero;
    }

    public @NotBlank(message = "Complemento é obrigatório") String getComplemento() {
        return complemento;
    }

    public void setComplemento(@NotBlank(message = "Complemento é obrigatório") String complemento) {
        this.complemento = complemento;
    }
}
