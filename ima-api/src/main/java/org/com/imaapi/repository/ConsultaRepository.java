package org.com.imaapi.repository;
import org.com.imaapi.model.consulta.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {
    List<Consulta> findByAssistido_IdUsuarioAndHorarioAfterOrderByHorarioAsc(Integer idUsuario, LocalDateTime agora);

    List<Consulta> findByVoluntario_IdUsuarioAndHorarioAfterOrderByHorarioAsc(Integer idUsuario, LocalDateTime agora);
    
    List<Consulta> findByVoluntario_IdUsuarioAndHorarioBetween(Integer idUsuario, LocalDateTime inicio, LocalDateTime fim);

}
