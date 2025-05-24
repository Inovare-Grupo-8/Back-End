package org.com.imaapi.model.pagamento.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PixPaymentResponse {
    private String transactionId;
    private String status;
    private String message;
}
