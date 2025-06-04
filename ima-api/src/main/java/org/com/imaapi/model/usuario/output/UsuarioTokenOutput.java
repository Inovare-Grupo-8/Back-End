package org.com.imaapi.model.usuario.output;

import lombok.Data;
import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.enums.Funcao;
import org.com.imaapi.model.enums.Genero;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UsuarioTokenOutput extends BaseUsuarioOutput {
    private String token;
    private Integer idFicha;
    private TipoUsuario tipo;
    private Funcao funcao;
    private String sobrenome;
    private String profissao;
    private String cpf;
    private LocalDate dataNascimento;
    private Double renda;
    private Genero genero;
    private LocalDate dataCadastro;
    private EnderecoOutput endereco;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private Integer versao;
}
