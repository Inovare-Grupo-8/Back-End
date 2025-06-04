package org.com.imaapi.model.consulta;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "feedback_consulta")
public class FeedbackConsulta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_feedback")
    private Integer idFeedback;

    @ManyToOne
    @JoinColumn(name = "fk_consulta")
    private Consulta consulta;

    @Column(name = "comentario")
    private String comentario;

    @Column(name = "dt_feedback")
    private LocalDateTime dtFeedback;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Integer version;
}
