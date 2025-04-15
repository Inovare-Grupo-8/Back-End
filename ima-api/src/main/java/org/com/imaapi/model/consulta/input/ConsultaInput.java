package org.com.imaapi.model.consulta.input;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.com.imaapi.model.enums.ModalidadeConsulta;
import org.com.imaapi.model.enums.StatusConsulta;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ConsultaInput {
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime horario;
    @NotBlank(message="Status não pode estar em branco")
    private StatusConsulta status;

    @NotBlank(message="Indique como será a consulta, online ou presencial")
    private ModalidadeConsulta modalidade;

    @NotBlank(message="Informe o local da consulta")
    private String local;
    private String observacoes;
    private Integer idEspecialidade;
    private Integer idAssistido;
    private Integer idVoluntario;
}
