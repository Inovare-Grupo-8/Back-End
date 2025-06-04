package org.com.imaapi.model.usuario;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;
import org.com.imaapi.model.usuario.input.TelefoneInput;

@Data
@Entity
@Table(name = "telefone")
public class Telefone {
    @Id
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

    public static Telefone of(TelefoneInput telefoneInput, Integer idFicha) {
        Telefone telefone = new Telefone();
        telefone.setIdTelefone(idFicha);
        telefone.setDdd(telefoneInput.getDdd());
        telefone.setPrefixo(telefoneInput.getPrefixo());
        telefone.setSufixo(telefoneInput.getSufixo());
        telefone.setWhatsapp(telefoneInput.getWhatsapp());
        return telefone;
    }
}