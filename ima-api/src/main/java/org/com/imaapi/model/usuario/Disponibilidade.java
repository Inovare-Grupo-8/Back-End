package org.com.imaapi.model.usuario;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "disponibilidade_voluntario")
public class Disponibilidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilidade")
    private Integer idDisponibilidade;

    @Column(name = "data_horario", nullable = false)
    private LocalDateTime dataHorario;

    @ManyToOne
    @JoinColumn(name = "fk_voluntario", referencedColumnName = "id_voluntario")
    private Voluntario voluntario;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}
