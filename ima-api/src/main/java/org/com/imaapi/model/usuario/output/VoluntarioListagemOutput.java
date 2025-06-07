package org.com.imaapi.model.usuario.output;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class VoluntarioListagemOutput {
    private Integer idUsuario;
    private Integer idVoluntario;
    private String nome;
    private String sobrenome;
    private String email;
    private String funcao;
    private String areaOrientacao;
    private LocalDate dataCadastro;
    private LocalDateTime ultimoAcesso;
    private Boolean ativo;
    
    public String getNomeCompleto() {
        return (nome != null ? nome : "") + 
               (sobrenome != null && !sobrenome.isEmpty() ? " " + sobrenome : "");
    }
}
