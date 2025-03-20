package org.com.imaapi.model.Usuario;

import jakarta.persistence.*;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "doacao")
public class Doacao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doacao")
    private Integer idDoacao;

    @Column(name = "valor")
    private BigDecimal valor;

    @Setter
    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    private Usuario usuario;
}