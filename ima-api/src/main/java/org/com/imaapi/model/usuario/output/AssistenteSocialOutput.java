package org.com.imaapi.model.usuario.output;

import lombok.Data;
import org.com.imaapi.model.usuario.Endereco;

@Data
public class AssistenteSocialOutput {
    private Integer idUsuario;
    private String nome;
    private String sobrenome;
    private String crp;
    private String especialidade;
    private String telefone;
    private String email;
    private String bio;
    private String fotoUrl;
    private Endereco endereco;
}
