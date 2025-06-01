package org.com.imaapi.repository;
import org.com.imaapi.model.consulta.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {
<<<<<<< HEAD
    List<Consulta> findByVoluntarioAndHorarioBetween(Integer idVoluntario, LocalDateTime inicio, LocalDateTime fim);
=======
    List<Consulta> findByVoluntario_IdUsuarioAndHorarioBetween(Integer idUsuario, LocalDateTime inicio, LocalDateTime fim);
>>>>>>> 9c893cfce19f60122119fdbd6957eacb2bb78e1c
}
