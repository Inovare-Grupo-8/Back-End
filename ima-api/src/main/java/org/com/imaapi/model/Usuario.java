package org.com.imaapi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column (name = "cpf")
    private String cpf;

    @Column (name = "email")
    private String email;

    @Column (name = "senha")
    private String senha;

    @Column (name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column (name = "renda")
    private Double renda;

    @Column (name = "genero")
    private String genero;

    @Column (name = "data_cadastro")
    private LocalDateTime dataCadastro;
}