package org.com.imaapi.model;

import jakarta.persistence.*;
import lombok.Data;
import org.com.imaapi.model.cargo.Funcao;

@Data
@Entity
@Table(name = "cargo")
public class Cargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCargo;

    @Enumerated(EnumType.STRING)
    private Funcao funcao;
}