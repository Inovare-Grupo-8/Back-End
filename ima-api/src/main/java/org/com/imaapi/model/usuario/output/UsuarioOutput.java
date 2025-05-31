package org.com.imaapi.model.usuario.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.com.imaapi.model.enums.Funcao;
import org.com.imaapi.model.enums.Genero;
import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.usuario.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioOutput {
    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private String senha;
    private LocalDate dataNascimento;
    private Double renda;
    private Genero genero;
    private TipoUsuario tipo;
    private Funcao funcao;
    private LocalDateTime dataCadastro;
    private EnderecoOutput endereco;

    public UsuarioOutput(Usuario usuario) {
        this.id = usuario.getIdUsuario();
        this.nome = usuario.getNome();
        this.cpf = usuario.getCpf();
        this.email = usuario.getEmail();
        this.senha = usuario.getSenha();
        this.dataNascimento = usuario.getDataNascimento();
        this.renda = usuario.getRenda();
        this.genero = usuario.getGenero();
        this.tipo = usuario.getTipo();
        this.dataCadastro = usuario.getDataCadastro();
        if (usuario.getEndereco() != null) {
            this.endereco = new EnderecoOutput();
            this.endereco.setCep(usuario.getEndereco().getCep());
            this.endereco.setNumero(usuario.getEndereco().getNumero());
            this.endereco.setComplemento(usuario.getEndereco().getComplemento());
        }
    }
}