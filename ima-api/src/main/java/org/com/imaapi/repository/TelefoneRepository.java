package org.com.imaapi.repository;

import org.com.imaapi.model.usuario.Telefone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelefoneRepository extends JpaRepository<Telefone, Integer> {
    void deleteByFichaIdFicha(Integer idFicha);
}
