package org.com.imaapi.service;

import org.com.imaapi.model.usuario.Disponibilidade;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface DisponibilidadeService {
    boolean criarDisponibilidade(Integer usuarioId, Disponibilidade disponibilidade);
    boolean atualizarDisponibilidade(Integer usuarioId, Disponibilidade disponibilidade);
}
