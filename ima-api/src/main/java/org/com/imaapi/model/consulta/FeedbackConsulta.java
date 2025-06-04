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

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Version
    @Column(name = "versao")
    private Integer versao;

    @PrePersist
    public void prePersist() {
        if (this.criadoEm == null) {
            this.criadoEm = LocalDateTime.now();
        }
        if (this.atualizadoEm == null) {
            this.atualizadoEm = LocalDateTime.now();
        }
        if (this.versao == null) {
            this.versao = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
