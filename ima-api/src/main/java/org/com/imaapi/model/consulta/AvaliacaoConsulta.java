package org.com.imaapi.model.consulta;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "avaliacao_consulta")
public class AvaliacaoConsulta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avaliacao")
    private Integer idAvaliacao;

    @ManyToOne
    @JoinColumn(name = "fk_consulta")
    private Consulta consulta;

    @Column(name = "nota")
    private Integer nota;

    @Column(name = "dt_avaliacao")
    private LocalDateTime dtAvaliacao;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Integer version;
}
