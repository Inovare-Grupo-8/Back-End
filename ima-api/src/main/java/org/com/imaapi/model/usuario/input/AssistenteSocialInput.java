package org.com.imaapi.model.usuario.input;

import lombok.Data;

@Data
public class AssistenteSocialInput {
    private String nome;
    private String sobrenome;
    private String crp;
    private String especialidade;
    private String telefone;
    private String email;
    private String senha;
    private String bio;
    private EnderecoInput endereco;
}
