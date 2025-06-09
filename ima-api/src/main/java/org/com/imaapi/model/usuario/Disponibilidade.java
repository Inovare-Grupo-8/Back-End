package org.com.imaapi.model.usuario;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "disponibilidade")
public class Disponibilidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilidade")
    private Integer idDisponibilidade;

    @Column(name = "dataHora")
    private LocalDateTime dataHora;

    @ManyToOne
    @JoinColumn(name = "fk_voluntario", referencedColumnName = "id_voluntario")
    private Voluntario voluntario;

    public Integer getIdDisponibilidade() {
        return idDisponibilidade;
    }

    public void setIdDisponibilidade(Integer idDisponibilidade) {
        this.idDisponibilidade = idDisponibilidade;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Voluntario getVoluntario() {
        return voluntario;
    }

    public void setVoluntario(Voluntario voluntario) {
        this.voluntario = voluntario;
    }
}
