package org.com.imaapi.model;

import jakarta.persistence.*;
import org.com.imaapi.model.enums.ModalidadeConsulta;
import org.com.imaapi.model.enums.StatusConsulta;

import java.time.LocalDateTime;

@Entity
@Table(name = "consulta")
public class Consulta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta")
    private Integer idConsulta;
    @Column(name = "horario")
    private LocalDateTime horario;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusConsulta status;
    @Enumerated(EnumType.STRING)
    @Column(name = "modalidade")
    private ModalidadeConsulta modalidade;
    @Column(name = "local")
    private String local;
    @Column(name = "observacoes")
    private String observacoes;
    @ManyToOne @JoinColumn(name = "fk_especialidade")
    private Especialidade especialidade;
    @ManyToOne @JoinColumn(name = "fk_cliente")
    private Usuario cliente;
    @ManyToOne @JoinColumn(name = "fk_especialista")
    private Usuario especialista;
}

