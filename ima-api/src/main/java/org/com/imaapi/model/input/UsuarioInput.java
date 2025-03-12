package org.com.imaapi.model.input;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.com.imaapi.model.cargo.Funcao;

@Data
public class UsuarioInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;
    private String nome;
    private String email;
    private String senha;
    private Boolean isVoluntario;
    private Funcao funcao;
}
