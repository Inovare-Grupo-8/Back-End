package org.com.imaapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "especialidade")
public class Especialidade {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especialidade")
    private Integer idEspecialidade;
    @Column(name = "nome")
    private String nome;
}