package org.com.imaapi.repository;

import org.com.imaapi.model.consulta.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {
    List<Consulta> findByAssistidoId(Integer idAssistido);

    List<Consulta> findByVoluntarioIdAndHorarioBetween(Integer voluntarioId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Consulta> findByVoluntarioIdAndHorarioBefore(Integer voluntarioId, LocalDateTime dataAtual);

    Optional<Consulta> findByIdConsultaAndVoluntarioId(Integer consultaId, Integer voluntarioId);

    List<Consulta> findTop3ByVoluntarioIdAndHorarioAfterOrderByHorarioAsc(Integer voluntarioId, LocalDateTime dataAtual);

    Optional<Consulta> findByIdConsultaAndAssistidoId(Integer consultaId, Integer assistidoId);

    List<Consulta> findTop3ByAssistidoIdAndHorarioAfterOrderByHorarioAsc(Integer assistidoId, LocalDateTime dataAtual);


}
