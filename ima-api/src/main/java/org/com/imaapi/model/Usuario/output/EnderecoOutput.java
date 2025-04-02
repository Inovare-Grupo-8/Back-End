package org.com.imaapi.model.Usuario.output;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class EnderecoOutput {
    private String cep;
    private String logradouro;
    private String complemento;
    private String numero;
    private String bairro;
    private String localidade;
    private String uf;
    private String estado;
    private String regiao;
}
