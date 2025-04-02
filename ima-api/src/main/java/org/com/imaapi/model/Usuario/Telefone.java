package org.com.imaapi.model.usuario;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

@Data
@Entity
@Table(name = "telefone")
public class Telefone {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_telefone")
    private Integer idTelefone;

    @Column(name = "ddd")
    private String ddd;

    @Column(name = "prefixo")
    private String prefixo;

    @Column(name = "sufixo")
    private String sufixo;

    @Column(name = "whatsapp")
    private Boolean whatsapp;

    @Setter
    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    private Usuario usuario;
}