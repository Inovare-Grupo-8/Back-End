package imaapi.controller;

import org.com.imaapi.controller.UsuarioController;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.input.UsuarioAutenticacaoInput;
import org.com.imaapi.model.usuario.input.UsuarioInput;
import org.com.imaapi.model.usuario.output.UsuarioListarOutput;
import org.com.imaapi.model.usuario.output.UsuarioTokenOutput;
import org.com.imaapi.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class UsuarioControllerTest {

    @InjectMocks
    private UsuarioController usuarioController;

    @Mock
    private UsuarioService usuarioService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCadastrarUsuario() {
        UsuarioInput usuarioInput = new UsuarioInput();
        Mockito.doNothing().when(usuarioService).cadastrarUsuario(any(UsuarioInput.class));

        ResponseEntity<Void> response = usuarioController.cadastrarUsuario(usuarioInput);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testCadastrarUsuarioComErro() {
        UsuarioInput usuarioInput = new UsuarioInput();
        Mockito.doThrow(new RuntimeException("Erro ao cadastrar usuário"))
                .when(usuarioService).cadastrarUsuario(any(UsuarioInput.class));

        try {
            usuarioController.cadastrarUsuario(usuarioInput);
        } catch (RuntimeException e) {
            assertEquals("Erro ao cadastrar usuário", e.getMessage());
        }
    }

    @Test
    public void testBuscarUsuario() {
        Integer id = 1;
        Usuario usuario = new Usuario();
        Mockito.when(usuarioService.buscaUsuario(eq(id))).thenReturn(Optional.of(usuario));

        ResponseEntity<Optional<Usuario>> response = usuarioController.buscaUsuario(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuario, response.getBody().orElse(null));
    }

    @Test
    public void testBuscarUsuarioNaoEncontrado() {
        Integer id = 1;
        Mockito.when(usuarioService.buscaUsuario(eq(id))).thenReturn(Optional.empty());

        ResponseEntity<Optional<Usuario>> response = usuarioController.buscaUsuario(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testAtualizarUsuario() {
        Integer id = 1;
        UsuarioInput usuarioInput = new UsuarioInput();
        UsuarioListarOutput usuarioAtualizado = new UsuarioListarOutput();
        Mockito.when(usuarioService.atualizarUsuario(eq(id), any(UsuarioInput.class))).thenReturn(usuarioAtualizado);

        ResponseEntity<UsuarioListarOutput> response = usuarioController.atualizarUsuario(id, usuarioInput);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(usuarioAtualizado, response.getBody());
    }

    @Test
    public void testAtualizarUsuarioNaoEncontrado() {
        Integer id = 1;
        UsuarioInput usuarioInput = new UsuarioInput();
        Mockito.when(usuarioService.atualizarUsuario(eq(id), any(UsuarioInput.class))).thenReturn(null);

        ResponseEntity<UsuarioListarOutput> response = usuarioController.atualizarUsuario(id, usuarioInput);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testExcluirUsuario() {
        Integer id = 1;
        Mockito.doNothing().when(usuarioService).deletarUsuario(eq(id));

        ResponseEntity<Void> response = usuarioController.deletarUsuario(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testExcluirUsuarioComErro() {
        Integer id = 1;
        Mockito.doThrow(new RuntimeException("Erro ao excluir usuário"))
                .when(usuarioService).deletarUsuario(eq(id));

        try {
            usuarioController.deletarUsuario(id);
        } catch (RuntimeException e) {
            assertEquals("Erro ao excluir usuário", e.getMessage());
        }
    }

    @Test
    public void testListarUsuarios() {
        List<UsuarioListarOutput> usuarios = Collections.singletonList(new UsuarioListarOutput());
        Mockito.when(usuarioService.buscarUsuarios()).thenReturn(usuarios);

        ResponseEntity<List<UsuarioListarOutput>> response = usuarioController.buscarUsuarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarios, response.getBody());
    }

    @Test
    public void testListarUsuariosVazio() {
        Mockito.when(usuarioService.buscarUsuarios()).thenReturn(Collections.emptyList());

        ResponseEntity<List<UsuarioListarOutput>> response = usuarioController.buscarUsuarios();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testAutenticarUsuario() {
        UsuarioAutenticacaoInput usuarioAutenticacaoInput = new UsuarioAutenticacaoInput();
        Usuario usuario = new Usuario();
        UsuarioTokenOutput tokenOutput = new UsuarioTokenOutput();
        Mockito.when(usuarioService.autenticar(any(Usuario.class))).thenReturn(tokenOutput);

        ResponseEntity<UsuarioTokenOutput> response = usuarioController.login(usuarioAutenticacaoInput);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tokenOutput, response.getBody());
    }

    @Test
    public void testAutenticarUsuarioNaoAutorizado() {
        UsuarioAutenticacaoInput usuarioAutenticacaoInput = new UsuarioAutenticacaoInput();
        Usuario usuario = new Usuario();
        Mockito.when(usuarioService.autenticar(any(Usuario.class))).thenReturn(null);

        ResponseEntity<UsuarioTokenOutput> response = usuarioController.login(usuarioAutenticacaoInput);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testCadastrarVoluntario() {
        UsuarioInput usuarioInput = new UsuarioInput();
        Mockito.doNothing().when(usuarioService).cadastrarVoluntario(any(UsuarioInput.class));

        ResponseEntity<Void> response = usuarioController.cadastrarVoluntario(usuarioInput);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testCadastrarVoluntarioComErro() {
        UsuarioInput usuarioInput = new UsuarioInput();
        Mockito.doThrow(new RuntimeException("Erro ao cadastrar voluntário"))
                .when(usuarioService).cadastrarVoluntario(any(UsuarioInput.class));

        try {
            usuarioController.cadastrarVoluntario(usuarioInput);
        } catch (RuntimeException e) {
            assertEquals("Erro ao cadastrar voluntário", e.getMessage());
        }
    }

}