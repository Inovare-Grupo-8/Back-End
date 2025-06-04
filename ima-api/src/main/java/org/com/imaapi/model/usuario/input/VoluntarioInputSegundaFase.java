package org.com.imaapi.model.usuario.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.com.imaapi.model.enums.Funcao;
import java.time.LocalDate;

@Data
public class VoluntarioInputSegundaFase {
    @NotNull(message = "CPF não pode ser nulo")
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos")
    private String cpf;
    
    @NotNull(message = "Data de nascimento não pode ser nula")
    @PastOrPresent(message = "Data de nascimento inválida")
    private LocalDate dataNascimento;
    
    @NotNull(message = "O gênero deve ser informado")
    private String genero;
    
    private Double renda;
    
    @NotNull(message = "A função do voluntário deve ser informada")
    private Funcao funcao;
    
    private String profissao;
    
    @NotNull(message = "Informações do endereço são obrigatórias")
    private EnderecoInput endereco;
    
    @NotNull(message = "Informações do telefone são obrigatórias")
    private TelefoneInput telefone;
}