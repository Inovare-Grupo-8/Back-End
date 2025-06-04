package org.com.imaapi.repository;

import org.com.imaapi.model.usuario.Ficha;
import org.com.imaapi.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FichaRepository extends JpaRepository<Ficha, Integer> {
    Optional<Usuario> findByNome(String nome);
}
