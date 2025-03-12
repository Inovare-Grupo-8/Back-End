package org.com.imaapi.model;

import jakarta.persistence.*;
import lombok.Data;
import org.com.imaapi.model.cargo.Funcao;

@Data
@Entity
@Table(name = "voluntario")
public class Voluntario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_voluntario")
    private Long idVoluntario;

    @Enumerated(EnumType.STRING)
    @Column(name = "funcao")
    private Funcao funcao;

    @ManyToOne
    @JoinColumn(name = "fk_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;

    public void setFkUsuario(Long fkUsuario) {
        this.usuario = new Usuario();
        this.usuario.setIdUsuario(fkUsuario);
    }
}