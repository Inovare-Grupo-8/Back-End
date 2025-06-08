package org.com.imaapi.service.impl;

import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.model.usuario.Disponibilidade;
import org.com.imaapi.model.usuario.Voluntario;
import org.com.imaapi.repository.ConsultaRepository;
import org.com.imaapi.repository.DisponibilidadeRepository;
import org.com.imaapi.repository.VoluntarioRepository;
import org.com.imaapi.service.DisponibilidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DisponibilidadeServiceImpl implements DisponibilidadeService {

    private final DisponibilidadeRepository disponibilidadeRepository;
    private final VoluntarioRepository voluntarioRepository;

    public DisponibilidadeServiceImpl(DisponibilidadeRepository disponibilidadeRepository, VoluntarioRepository voluntarioRepository) {
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.voluntarioRepository = voluntarioRepository;
    }

    @Override
    public boolean criarDisponibilidade(Integer usuarioId, Disponibilidade disponibilidade) {
        Voluntario voluntario = voluntarioRepository.findByUsuario_IdUsuario(usuarioId);
        if (voluntario == null) {
            throw new IllegalArgumentException("Voluntário não encontrado para o usuário ID: " + usuarioId);
        }
        disponibilidade.setVoluntario(voluntario);
        disponibilidadeRepository.save(disponibilidade);
        return true;
    }

    @Override
    public boolean atualizarDisponibilidade(Integer usuarioId, Disponibilidade disponibilidade) {
        Disponibilidade disponibilidadeExistente = disponibilidadeRepository.findById(disponibilidade.getIdDisponibilidade())
                .orElseThrow(() -> new IllegalArgumentException("Disponibilidade não encontrada para ID: " + disponibilidade.getIdDisponibilidade()));
        disponibilidadeExistente.setDataHorario(disponibilidade.getDataHorario());
        disponibilidadeRepository.save(disponibilidadeExistente);
        return true;
    }
}