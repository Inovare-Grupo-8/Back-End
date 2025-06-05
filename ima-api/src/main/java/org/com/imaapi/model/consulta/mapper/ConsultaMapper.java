package org.com.imaapi.model.consulta.mapper;

import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.model.consulta.dto.ConsultaDto;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.model.enums.ModalidadeConsulta;
import org.com.imaapi.model.enums.StatusConsulta;
import org.com.imaapi.model.especialidade.Especialidade;
import org.com.imaapi.model.usuario.Usuario;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class ConsultaMapper {    public static ConsultaDto toDto(Consulta consulta) {
        ConsultaDto dto = new ConsultaDto();
        dto.setIdConsulta(consulta.getIdConsulta());
        dto.setHorario(consulta.getHorario());
        dto.setStatus(consulta.getStatus().name());
        dto.setModalidade(consulta.getModalidade().name());
        dto.setLocal(consulta.getLocal());
        dto.setObservacoes(consulta.getObservacoes());
        
        // Set especialidade information
        dto.setIdEspecialidade(consulta.getEspecialidade() != null ? consulta.getEspecialidade().getId() : null);
        dto.setNomeEspecialidade(consulta.getEspecialidade() != null ? consulta.getEspecialidade().getNome() : null);
        
        // Set voluntario information (new fields)
        dto.setIdVoluntario(consulta.getVoluntario() != null ? consulta.getVoluntario().getIdUsuario() : null);
        dto.setNomeVoluntario(consulta.getVoluntario() != null && consulta.getVoluntario().getFicha() != null ? 
                consulta.getVoluntario().getFicha().getNome() : null);
                
        // Set assistido information (new fields)
        dto.setIdAssistido(consulta.getAssistido() != null ? consulta.getAssistido().getIdUsuario() : null);
        dto.setNomeAssistido(consulta.getAssistido() != null && consulta.getAssistido().getFicha() != null ? 
                consulta.getAssistido().getFicha().getNome() : null);
                
        // Set legacy fields for backward compatibility
        dto.setIdEspecialista(consulta.getVoluntario() != null ? consulta.getVoluntario().getIdUsuario() : null);
        dto.setNomeEspecialista(consulta.getVoluntario() != null && consulta.getVoluntario().getFicha() != null ? 
                consulta.getVoluntario().getFicha().getNome() : null);
        dto.setIdCliente(consulta.getAssistido() != null ? consulta.getAssistido().getIdUsuario() : null);
        dto.setNomeCliente(consulta.getAssistido() != null && consulta.getAssistido().getFicha() != null ?                consulta.getAssistido().getFicha().getNome() : null);
                
        dto.setFeedbackStatus(consulta.getFeedbackStatus());
        dto.setAvaliacaoStatus(consulta.getAvaliacaoStatus());
        
        return dto;
    }public static Consulta toEntity(ConsultaInput input) {
        if (input == null) {
            throw new IllegalArgumentException("O objeto ConsultaInput não pode ser nulo");
        }
        
        Consulta consulta = new Consulta();
        
        // Set basic fields with null checks
        if (input.getHorario() == null) {
            throw new IllegalArgumentException("O horário da consulta é obrigatório");
        }
        consulta.setHorario(input.getHorario());
        
        if (input.getStatus() == null) {
            throw new IllegalArgumentException("O status da consulta é obrigatório");
        }
        consulta.setStatus(input.getStatus());
        
        if (input.getModalidade() == null) {
            throw new IllegalArgumentException("A modalidade da consulta é obrigatória");
        }
        consulta.setModalidade(input.getModalidade());
        
        if (input.getLocal() == null || input.getLocal().trim().isEmpty()) {
            throw new IllegalArgumentException("O local da consulta é obrigatório");
        }
        consulta.setLocal(input.getLocal());
        
        // Optional field
        consulta.setObservacoes(input.getObservacoes());
        
        // Handle especialidade - prefer complete entity if available
        if (input.getEspecialidade() != null) {
            if (input.getEspecialidade().getId() == null) {
                throw new IllegalArgumentException("O ID da especialidade é obrigatório quando fornecido como objeto");
            }
            consulta.setEspecialidade(input.getEspecialidade());
        }
        
        // Handle assistido - prefer complete entity if available
        if (input.getAssistido() != null) {
            if (input.getAssistido().getIdUsuario() == null) {
                throw new IllegalArgumentException("O ID do assistido é obrigatório quando fornecido como objeto");
            }
            consulta.setAssistido(input.getAssistido());
        }
        
        // Handle voluntario - prefer complete entity if available
        if (input.getVoluntario() != null) {
            if (input.getVoluntario().getIdUsuario() == null) {
                throw new IllegalArgumentException("O ID do voluntário é obrigatório quando fornecido como objeto");
            }
            consulta.setVoluntario(input.getVoluntario());
        }
        
        return consulta;
    }public static ConsultaOutput toOutput(ConsultaDto dto) {
        ConsultaOutput output = new ConsultaOutput();
        output.setHorario(dto.getHorario());
        output.setStatus(StatusConsulta.valueOf(dto.getStatus()));
        output.setModalidade(ModalidadeConsulta.valueOf(dto.getModalidade()));
        output.setLocal(dto.getLocal());
        output.setObservacoes(dto.getObservacoes());
        
        // Inclui dados temporários para identificação
        if (dto.getIdEspecialidade() != null) {
            Especialidade especialidade = new Especialidade();
            especialidade.setId(dto.getIdEspecialidade());
            especialidade.setNome(dto.getNomeEspecialidade());
            output.setEspecialidade(especialidade);
        }
        
        if (dto.getIdCliente() != null) {
            Usuario assistido = new Usuario();
            assistido.setIdUsuario(dto.getIdCliente());
            // Não temos todas as informações do usuário aqui, então apenas o ID é definido
            output.setAssistido(assistido);
        }
        
        if (dto.getIdEspecialista() != null) {
            Usuario voluntario = new Usuario();
            voluntario.setIdUsuario(dto.getIdEspecialista());
            // Não temos todas as informações do usuário aqui, então apenas o ID é definido
            output.setVoluntario(voluntario);
        }
        
        return output;
    }
}
