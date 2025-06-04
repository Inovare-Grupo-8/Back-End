package org.com.imaapi.repository;

import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.Voluntario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoluntarioRepository extends JpaRepository<Voluntario, Integer> {
    Optional<Voluntario> findByUsuario(Usuario usuario);
}