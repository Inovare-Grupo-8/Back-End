package org.com.imaapi.repository;

import org.com.imaapi.model.usuario.Disponibilidade;
import org.com.imaapi.model.usuario.Voluntario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
public interface DisponibilidadeRepository extends JpaRepository<Disponibilidade, Integer> {

    List<Disponibilidade> findByUserAndDia(String user, LocalDate dia);
    List<Disponibilidade> findByVoluntarioAndDataHorarioBetween(Voluntario voluntario, LocalDateTime inicio, LocalDateTime fim);

}
