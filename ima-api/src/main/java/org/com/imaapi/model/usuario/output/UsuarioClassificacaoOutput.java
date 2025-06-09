package org.com.imaapi.model.usuario.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.com.imaapi.model.enums.Genero;
import org.com.imaapi.model.enums.TipoUsuario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioClassificacaoOutput {
    private Integer id;
    private String email;
    private TipoUsuario tipo;
    private LocalDate dataCadastro;
    
    // Dados da Ficha
    private String nome;
    private String sobrenome;    private String cpf;    private LocalDate dataNascimento;
    private BigDecimal rendaMinima;
    private BigDecimal rendaMaxima;
    private Genero genero;
    private String areaInteresse;
    private String profissao;
    
    // Dados do Endereço
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
    
    // Telefones
    private List<TelefoneOutput> telefones;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TelefoneOutput {
        private String numero;
        private String tipo;
    }
}
