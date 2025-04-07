package org.com.imaapi.repository;

import org.com.imaapi.model.consulta.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {

    @Query(value = "SELECT * FROM consulta WHERE fk_cliente = :idAssistido", nativeQuery = true)
    List<Consulta> findByAssistidoId(Integer idAssistido);

    @Query(value = "SELECT * FROM consulta WHERE fk_especialista = :idVoluntario", nativeQuery = true)
    List<Consulta> findByVoluntarioId(Integer idVoluntario);
}