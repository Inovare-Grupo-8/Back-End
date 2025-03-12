package org.com.imaapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id_usuario")
    private Long idUsuario;

    @Column (name = "nome")
    private String nome;

    @Column (name = "email")
    private String email;

    @Column (name = "senha")
    private String senha;
}