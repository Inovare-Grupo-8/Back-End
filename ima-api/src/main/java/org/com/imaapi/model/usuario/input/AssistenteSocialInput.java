package org.com.imaapi.model.usuario.input;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;
import lombok.Data;
import org.com.imaapi.model.enums.Funcao;
import org.com.imaapi.model.enums.TipoUsuario;

@Data
public class AssistenteSocialInput {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Sobrenome é obrigatório")
    private String sobrenome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    @NotBlank(message = "CPF é obrigatório")
    @CPF(message = "CPF inválido")
    private String cpf;

    @NotBlank(message = "Data de nascimento é obrigatória")
    private String dataNascimento;    @NotBlank(message = "Gênero é obrigatório")
    private String genero;

    private TipoUsuario tipo = TipoUsuario.ADMINISTRADOR;
    private Funcao funcao = Funcao.ASSISTENCIA_SOCIAL;

    @NotBlank(message = "Profissão é obrigatória")
    private String profissao;

    @NotBlank(message = "DDD é obrigatório")
    private String ddd;

    @NotBlank(message = "Número é obrigatório")
    private String numero;

    @NotBlank(message = "CEP é obrigatório")
    private String cep;

    private String complemento;

    private String crp;

    private String especialidade;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    private String bio;
}
