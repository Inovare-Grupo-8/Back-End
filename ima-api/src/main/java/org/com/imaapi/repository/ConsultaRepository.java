package org.com.imaapi.repository;

import org.com.imaapi.model.consulta.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {
    List<Consulta> findByVoluntario_IdUsuarioAndHorarioBetween(Integer idUsuario, LocalDateTime inicio, LocalDateTime fim);

    List<Consulta> findByAssistido_IdUsuarioAndHorarioBetween(Integer idUsuario, LocalDateTime inicio, LocalDateTime fim);

    List<Consulta> findByVoluntario_IdUsuarioAndStatusOrderByHorarioDesc(Integer idUsuario, String status);

    List<Consulta> findByAssistido_IdUsuarioAndStatusOrderByHorarioDesc(Integer idUsuario, String status);
}
