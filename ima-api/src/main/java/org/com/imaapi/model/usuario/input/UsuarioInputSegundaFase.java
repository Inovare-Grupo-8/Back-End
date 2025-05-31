package org.com.imaapi.model.usuario.input;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

import org.com.imaapi.model.enums.Funcao;
import org.com.imaapi.model.enums.Genero;
import org.com.imaapi.model.enums.TipoUsuario;

@Data
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsuarioInputSegundaFase {
    @NotNull(message = "Valor nulo, insíra um valor válido")
    private Double renda;

    @NotNull(message = "O gênero deve ser informado")
    private Genero genero;

    @NotNull(message="Confirme se é um voluntário")
    private Boolean isVoluntario;

    private Funcao funcao;
    private TipoUsuario tipo;

    @NotBlank(message = "O CEP não pode estar em branco")
    @Pattern(regexp = "\\d{8}|\\d{5}-\\d{3}", message = "O CEP deve estar no formato 00000000 ou 00000-000")
    private String cep;

    private String numero;

    private String complemento;
}
