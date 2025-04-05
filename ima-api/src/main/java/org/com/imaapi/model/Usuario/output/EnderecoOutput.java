package org.com.imaapi.model.Usuario.output;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Table(name = "enderecos")
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
