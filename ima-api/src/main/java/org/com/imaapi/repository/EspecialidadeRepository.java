package org.com.imaapi.repository;

import org.com.imaapi.model.usuario.Especialidade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspecialidadeRepository extends JpaRepository<Especialidade, Integer> {
}
