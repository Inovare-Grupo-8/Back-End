package org.com.imaapi.model.usuario;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "doacao")
public class Doacao {    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doacao")
    private Integer idDoacao;

    @Column(name = "valor")
    private BigDecimal valor;

    @Setter
    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    private Usuario usuario;
}