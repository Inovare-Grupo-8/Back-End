package org.com.imaapi.model.consulta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para retornar contagem de consultas
 * Usado pelos endpoints que retornam apenas o número de consultas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaCountDto {
    private Integer count;
}
