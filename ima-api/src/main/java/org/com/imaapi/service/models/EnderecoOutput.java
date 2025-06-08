package org.com.imaapi.service.models;

import lombok.Data;

@Data
public class EnderecoOutput {
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;
    private String numero;
}
