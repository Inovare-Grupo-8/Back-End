package org.com.imaapi.repository;

import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.model.enums.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {
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

   List<Consulta> findByAssistido_IdUsuarioAndHorarioAfterOrderByHorarioAsc(Integer idUsuario, LocalDateTime horario);
    List<Consulta> findByVoluntario_IdUsuarioAndHorarioAfterOrderByHorarioAsc(Integer idUsuario, LocalDateTime horario);
    List<Consulta> findByHorarioBetween(LocalDateTime inicio, LocalDateTime fim);


    // Método para buscar consultas por assistido ou voluntario
    List<Consulta> findByAssistido_IdUsuarioOrVoluntario_IdUsuario(Integer idAssistido, Integer idVoluntario);

}
