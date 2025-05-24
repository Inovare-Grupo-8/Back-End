package org.com.imaapi.model.pagamento.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {
    private String client_id;
    private String client_secret;
    private String grant_type = "client_credentials";
}
