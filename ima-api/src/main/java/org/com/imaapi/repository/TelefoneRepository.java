package org.com.imaapi.repository;

import org.com.imaapi.model.usuario.Telefone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TelefoneRepository extends JpaRepository<Telefone, Integer> {
    void deleteByFichaIdFicha(Integer idFicha);
    List<Telefone> findByFichaIdFicha(Integer idFicha);
}
