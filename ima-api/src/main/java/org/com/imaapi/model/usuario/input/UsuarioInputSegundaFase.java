package org.com.imaapi.model.usuario.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.com.imaapi.model.enums.Funcao;
import org.com.imaapi.model.enums.TipoUsuario;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsuarioInputSegundaFase {
    @NotNull(message = "Data de nascimento não pode ser nula")
    @PastOrPresent(message = "Data de nascimento inválida")
    private LocalDate dataNascimento;

    private Double rendaMinima;
    
    private Double rendaMaxima;

    @NotNull(message = "O gênero deve ser informado")
    private String genero;

    @NotNull(message = "O tipo de usuário deve ser informado")
    private TipoUsuario tipo;

    @NotNull(message = "Informações do endereço são obrigatórias")
    private EnderecoInput endereco;

    @NotNull(message = "Informações do telefone são obrigatórias")
    private TelefoneInput telefone;

    private Funcao funcao;

    private String areaOrientacao;

    private String comoSoube;

    private String profissao;
}
