package org.com.imaapi.repository;

import org.com.imaapi.model.consulta.FeedbackConsulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackConsultaRepository extends JpaRepository<FeedbackConsulta, Integer> {
    List<FeedbackConsulta> findByConsulta_Voluntario_IdUsuario(Integer idVoluntario);

    List<FeedbackConsulta> findByConsulta_Assistido_IdUsuario(Integer idAssistido);
}
