package org.com.imaapi.model.especialidade;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity(name = "EspecialidadeModel")
@Table(name = "especialidade")
public class Especialidade {    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especialidade")
    private Integer idEspecialidade;

    @Column(name = "nome", nullable = false, unique = true, length = 45)
    private String nome;

    @CreationTimestamp
    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Version
    @Column(name = "versao")
    private Integer versao;
    
    public Integer getId() {
        return this.idEspecialidade;
    }
    
    public void setId(Integer id) {
        this.idEspecialidade = id;
    }
}
