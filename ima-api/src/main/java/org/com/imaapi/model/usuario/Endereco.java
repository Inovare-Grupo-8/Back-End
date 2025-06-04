package org.com.imaapi.model.usuario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.imaapi.model.usuario.output.EnderecoOutput;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "endereco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endereco")
    private Integer idEndereco;

    @Column(name = "cep", length = 8)
    private String cep;

    @Column(name = "logradouro", length = 200)
    private String logradouro;

    @Column(name = "bairro", length = 45)
    private String bairro;

    @Column(name = "numero", length = 10)
    private String numero;

    @Column(name = "cidade", length = 45)
    private String cidade;

    @Column(name = "uf", length = 2)
    private String uf;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Integer version;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static Endereco of(EnderecoOutput enderecoOutput) {
        Endereco endereco = new Endereco();
        endereco.setCep(enderecoOutput.getCep());
        endereco.setLogradouro(enderecoOutput.getLogradouro());
        endereco.setBairro(enderecoOutput.getBairro());
        endereco.setNumero(enderecoOutput.getNumero());
        endereco.setUf(enderecoOutput.getUf());
        return endereco;
    }
}