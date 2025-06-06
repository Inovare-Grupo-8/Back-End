package org.com.imaapi.model.consulta.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.com.imaapi.model.enums.ModalidadeConsulta;
import org.com.imaapi.model.enums.StatusConsulta;
import org.com.imaapi.model.especialidade.Especialidade;
import org.com.imaapi.model.usuario.Usuario;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaInput {   
    @NotNull(message = "O horário da consulta é obrigatório")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime horario;

    @NotNull(message = "Status não pode estar em branco")
    private StatusConsulta status;

    @NotNull(message = "Indique como será a consulta, online ou presencial")
    private ModalidadeConsulta modalidade;

    @NotBlank(message = "Informe o local da consulta")
    private String local;
    
    private String observacoes;

    private Integer idEspecialidade;
    
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Especialidade especialidade;
    
    private Integer idAssistido;
    
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "senha", "tipo", "ficha.endereco"})
    private Usuario assistido;
    
    private Integer idVoluntario;
    
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "senha", "tipo", "ficha.endereco"})
    private Usuario voluntario;
}
