package org.com.imaapi.repository;

import org.com.imaapi.model.Usuario.Especialidade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspecialidadeRepository extends JpaRepository<Especialidade, Integer> {
}
