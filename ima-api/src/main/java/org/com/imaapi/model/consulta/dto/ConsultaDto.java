package org.com.imaapi.model.consulta.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ConsultaDto {    
    private Integer idConsulta;
    private LocalDateTime horario;
    private String status;
    private String modalidade;
    private String local;
    private String observacoes;
    private Integer idEspecialidade;
    private String nomeEspecialidade;
    private Integer idVoluntario;
    private String nomeVoluntario;
    private Integer idAssistido;
    private String nomeAssistido;
    private Integer idEspecialista;
    private String nomeEspecialista;
    private Integer idCliente;
    private String nomeCliente;
    private String feedbackStatus;
    private String avaliacaoStatus;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
