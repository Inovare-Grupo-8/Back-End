package org.com.imaapi.model.consulta;

import jakarta.persistence.*;
import lombok.*;
import org.com.imaapi.model.enums.ModalidadeConsulta;
import org.com.imaapi.model.enums.StatusConsulta;
import org.com.imaapi.model.especialidade.Especialidade;
import org.com.imaapi.model.usuario.Usuario;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "consulta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Consulta {    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToOne
    @JoinColumn(name = "fk_especialidade")
    private Especialidade especialidade;

    @OneToOne
    @JoinColumn(name = "fk_cliente")
    private Usuario assistido;

    @OneToOne
    @JoinColumn(name = "fk_especialista")
    private Usuario voluntario;

    @Column(name = "feedback_status")
    private String feedbackStatus = "PENDENTE";

    @Column(name = "avaliacao_status")
    private String avaliacaoStatus = "PENDENTE";
}