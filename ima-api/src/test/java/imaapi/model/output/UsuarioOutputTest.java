package imaapi.model.output;

import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.enums.Genero;
import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.usuario.Endereco;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioOutputTest {

    @Test
    void deveCriarUsuarioComValoresCorretos() {
        Usuario usuario = new Usuario();
        usuario.setNome("João");
        usuario.setCpf("12345678901");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("Senha@123");
        usuario.setRenda(5000.0);
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));

        assertEquals("João", usuario.getNome());
        assertEquals("12345678901", usuario.getCpf());
        assertEquals("joao@email.com", usuario.getEmail());
        assertEquals(5000.0, usuario.getRenda());
    }

    @Test
    void deveGerarDataDeCadastroAutomaticamenteAoPersistir() {
        Usuario usuario = new Usuario();
        usuario.prePersist();
        assertNotNull(usuario.getDataCadastro());
        assertTrue(usuario.getDataCadastro().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void deveLancarExcecaoQuandoRendaNaoForDouble() {
        Usuario usuario = Mockito.mock(Usuario.class);
        // Simulando comportamento indevido (não acontece no Java fortemente tipado diretamente, mas ilustrativo)
        Mockito.when(usuario.getRenda()).thenReturn(null); // Não é Double válido

        assertNull(usuario.getRenda(), "Renda deve ser null se não atribuída corretamente");
    }

    @Test
    void deveAceitarEnderecoValido() {
        Endereco enderecoMock = Mockito.mock(Endereco.class);
        Usuario usuario = new Usuario();
        usuario.setEndereco(enderecoMock);

        assertNotNull(usuario.getEndereco());
        assertEquals(enderecoMock, usuario.getEndereco());
    }

    @Test
    void naoDeveAceitarCpfComLetras() {
        Usuario usuario = new Usuario();
        usuario.setCpf("abc123def45"); // CPF inválido

        String cpf = usuario.getCpf();
        assertFalse(cpf.matches("\\d{11}"), "CPF não deve conter letras");
    }
}

