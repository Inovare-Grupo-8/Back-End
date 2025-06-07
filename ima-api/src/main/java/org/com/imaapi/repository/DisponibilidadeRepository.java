package org.com.imaapi.repository;

import org.com.imaapi.model.usuario.Disponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DisponibilidadeRepository extends JpaRepository<Disponibilidade, Integer> {
}
