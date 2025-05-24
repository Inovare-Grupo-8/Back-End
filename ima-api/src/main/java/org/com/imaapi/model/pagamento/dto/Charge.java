package org.com.imaapi.model.pagamento.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Charge {
    private String id;
    private String status;
    private String created_at;
}


