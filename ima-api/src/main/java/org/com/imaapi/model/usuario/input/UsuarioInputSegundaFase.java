package org.com.imaapi.model.usuario.input;
import jakarta.validation.constraints.*;
import lombok.*;

import org.com.imaapi.model.enums.Funcao;
import org.com.imaapi.model.enums.TipoUsuario;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsuarioInputSegundaFase {
    @NotNull(message = "CPF não pode ser nulo")
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos")
    private String cpf;

    @NotNull(message = "Data de nascimento não pode ser nula")
    @PastOrPresent(message = "Data de nascimento inválida")
    private LocalDate dataNascimento;    
    
    private Double renda;

    @NotNull(message = "O gênero deve ser informado")
    private String genero;

    @NotNull(message="O tipo de usuário deve ser informado")
    private TipoUsuario tipo;

    @NotNull(message = "Informações do endereço são obrigatórias")
    private EnderecoInput endereco;

    @NotNull(message = "Informações do telefone são obrigatórias")
    private TelefoneInput telefone;    
    
    private Funcao funcao;


}
