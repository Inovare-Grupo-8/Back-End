package org.com.imaapi.repository;

import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.model.enums.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {
    List<Consulta> findByAssistidoId(Integer idAssistido);

    List<Consulta> findByVoluntarioIdAndHorarioBetween(Integer voluntarioId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Consulta> findByVoluntarioIdAndHorarioBefore(Integer voluntarioId, LocalDateTime dataAtual);

    Optional<Consulta> findByIdConsultaAndVoluntarioId(Integer consultaId, Integer voluntarioId);

    List<Consulta> findTop3ByVoluntarioIdAndHorarioAfterOrderByHorarioAsc(Integer voluntarioId, LocalDateTime dataAtual);

    Optional<Consulta> findByIdConsultaAndAssistidoId(Integer consultaId, Integer assistidoId);

    List<Consulta> findTop3ByAssistidoIdAndHorarioAfterOrderByHorarioAsc(Integer assistidoId, LocalDateTime dataAtual);


    List<Consulta> findByVoluntario_IdUsuarioAndHorarioBetween(Integer idUsuario, LocalDateTime inicio, LocalDateTime fim);
    List<Consulta> findByAssistido_IdUsuarioAndHorarioBetween(Integer idUsuario, LocalDateTime inicio, LocalDateTime fim);
    List<Consulta> findByVoluntario_IdUsuarioAndStatusOrderByHorarioDesc(Integer idUsuario, String status);
    List<Consulta> findByAssistido_IdUsuarioAndStatusOrderByHorarioDesc(Integer idUsuario, String status);
    // Métodos existentes
    List<Consulta> findByVoluntario_IdUsuarioAndHorarioBetween(Integer idUsuario, LocalDateTime inicio, LocalDateTime fim);

    List<Consulta> findByAssistido_IdUsuarioAndHorarioBetween(Integer idUsuario, LocalDateTime inicio, LocalDateTime fim);

    List<Consulta> findByVoluntario_IdUsuarioAndStatusOrderByHorarioDesc(Integer idUsuario, String status);

    List<Consulta> findByAssistido_IdUsuarioAndStatusOrderByHorarioDesc(Integer idUsuario, String status);

    // Métodos para buscar todas as consultas de um usuário
    List<Consulta> findByVoluntario_IdUsuario(Integer idUsuario);

    List<Consulta> findByAssistido_IdUsuario(Integer idUsuario);

    // Novos métodos para filtrar por status
    List<Consulta> findByVoluntario_IdUsuarioAndStatusIn(Integer idUsuario, List<StatusConsulta> statusList);

    List<Consulta> findByAssistido_IdUsuarioAndStatusIn(Integer idUsuario, List<StatusConsulta> statusList);

    List<Consulta> findByVoluntario_IdUsuarioAndHorarioBetweenAndStatusIn(
            Integer idUsuario, LocalDateTime inicio, LocalDateTime fim, List<StatusConsulta> statusList);

    List<Consulta> findByAssistido_IdUsuarioAndHorarioBetweenAndStatusIn(
            Integer idUsuario, LocalDateTime inicio, LocalDateTime fim, List<StatusConsulta> statusList);
}
