package org.com.imaapi.model.usuario.output;

import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Table(name = "endereco")
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