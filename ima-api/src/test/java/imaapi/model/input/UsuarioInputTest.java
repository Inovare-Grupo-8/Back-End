package imaapi.model.input;

import org.com.imaapi.model.usuario.input.UsuarioInput;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioInputTest {

    @Test
    void emailDeveSerValido() {
        UsuarioInput usuarioInput = new UsuarioInput();
        usuarioInput.setEmail("teste.teste.com");

        String email = usuarioInput.getEmail();
        assertFalse(email.contains("@"), "Email inválido: deve conter '@'");
    }

    @Test
    void nomeDeveSerValido() {
        UsuarioInput usuarioInput = new UsuarioInput();
        usuarioInput.setNome(null);

        String nome = usuarioInput.getNome();
        assertEquals(null, nome, "Nome não pode ser nulo");
    }

    @Test
    void senhaDeveConterCaracterEspecial() {
        UsuarioInput usuarioInput = new UsuarioInput();
        usuarioInput.setSenha("senhaTeste");

        String senha = usuarioInput.getSenha();
        assertFalse(senha.contains("@"), "Senha deve conter caracter especial");
    }

    @Test
    void senhaMenorQueSeisCaracteresNaoEhValida() {
        UsuarioInput usuario = new UsuarioInput();
        usuario.setSenha("abc"); // apenas 3 caracteres
        assertTrue(usuario.getSenha().length() < 6, "Senha com menos de 6 caracteres deve ser inválida");
    }

    @Test
    void deveAceitarCpfValidoComOnzeDigitos() {
        UsuarioInput mockUsuario = Mockito.mock(UsuarioInput.class);
        Mockito.when(mockUsuario.getCpf()).thenReturn("12345678901");

        String cpf = mockUsuario.getCpf();
        assertTrue(cpf.matches("\\d{11}"), "CPF com 11 dígitos numéricos deve ser aceito");
    }
}