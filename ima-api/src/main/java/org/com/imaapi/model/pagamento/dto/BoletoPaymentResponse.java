package org.com.imaapi.model.pagamento.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoletoPaymentResponse {
    private String nome;
    private String cpfCnpj;
    private Double valor;
    private String vencimento; // Formato ISO: yyyy-MM-dd
    private String descricao;
}
