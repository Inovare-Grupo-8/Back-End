package org.com.imaapi.model.pagamento.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TEDPaymentResponse {
    private String nomeFavorecido;
    private String cpfCnpj;
    private String banco; // código do banco, ex: "001" para Banco do Brasil
    private String agencia;
    private String numeroConta;
    private String tipoConta; // "corrente" ou "poupanca"
    private Double valor;
    private String descricao;
}