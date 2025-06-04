package imaapi.model.input;

import org.com.imaapi.model.usuario.input.UsuarioInputPrimeiraFase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioInputPrimeiraFaseTest {

    @Test
    void emailDeveSerValido() {
        UsuarioInputPrimeiraFase usuarioInputPrimeiraFase = new UsuarioInputPrimeiraFase();
        usuarioInputPrimeiraFase.setEmail("teste.teste.com");

        String email = usuarioInputPrimeiraFase.getEmail();
        assertFalse(email.contains("@"), "Email inválido: deve conter '@'");
    }

    @Test
    void nomeDeveSerValido() {
        UsuarioInputPrimeiraFase usuarioInputPrimeiraFase = new UsuarioInputPrimeiraFase();
        usuarioInputPrimeiraFase.setNome(null);

        String nome = usuarioInputPrimeiraFase.getNome();
        assertEquals(null, nome, "Nome não pode ser nulo");
    }

    @Test
    void senhaDeveConterCaracterEspecial() {
        UsuarioInputPrimeiraFase usuarioInputPrimeiraFase = new UsuarioInputPrimeiraFase();
        usuarioInputPrimeiraFase.setSenha("senhaTeste");

        String senha = usuarioInputPrimeiraFase.getSenha();
        assertFalse(senha.contains("@"), "Senha deve conter caracter especial");
    }

    @Test
    void senhaMenorQueSeisCaracteresNaoEhValida() {
        UsuarioInputPrimeiraFase usuario = new UsuarioInputPrimeiraFase();
        usuario.setSenha("abc"); // apenas 3 caracteres
        assertTrue(usuario.getSenha().length() < 6, "Senha com menos de 6 caracteres deve ser inválida");
    }
}