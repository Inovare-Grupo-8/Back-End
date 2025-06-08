package org.com.imaapi.model.usuario;

import jakarta.persistence.*;
import lombok.Data;
import org.com.imaapi.model.especialidade.Especialidade;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "voluntario_especialidade")
public class VoluntarioEspecialidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_voluntario_especialidade")
    private Integer idVoluntarioEspecialidade;

    @ManyToOne
    @JoinColumn(name = "fk_voluntario", nullable = false)
    private Voluntario voluntario;

    @ManyToOne
    @JoinColumn(name = "fk_especialidade", nullable = false)
    private Especialidade especialidade;

    @Column(name = "principal")
    private Boolean principal;

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
