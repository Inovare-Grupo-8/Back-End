package org.com.imaapi.repository;

import org.com.imaapi.model.usuario.Voluntario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoluntarioRepository extends JpaRepository<Voluntario, Integer> {
    Voluntario findByUsuario_IdUsuario(Integer usuarioId);
}