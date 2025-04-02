package org.com.imaapi.model.Usuario;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "especialidade")
public class Especialidade {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especialidade")
    private Integer idEspecialidade;
    @Column(name = "nome")
    private String nome;
}