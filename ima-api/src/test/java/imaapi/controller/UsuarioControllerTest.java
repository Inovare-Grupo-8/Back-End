package imaapi.controller;

import org.com.imaapi.controller.UsuarioController;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.input.UsuarioAutenticacaoInput;
import org.com.imaapi.model.usuario.input.UsuarioInputPrimeiraFase;
import org.com.imaapi.model.usuario.input.UsuarioInputSegundaFase;
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

//    @Test
//    public void testCadastrarUsuario() {
//        UsuarioInputPrimeiraFase usuarioInputPrimeiraFase = new UsuarioInputPrimeiraFase();
//        Usuario usuario = new Usuario();
//        Mockito.when(usuarioService.cadastrarPrimeiraFase(any(UsuarioInputPrimeiraFase.class))).thenReturn(usuario);
//
//        ResponseEntity<Usuario> response = usuarioController.cadastrarUsuarioFase1(usuarioInputPrimeiraFase);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals(usuario, response.getBody());
//    }

    @Test
    public void testCompletarCadastroUsuario() {        Integer id = 1;
        UsuarioInputSegundaFase usuarioInputSegundaFase = new UsuarioInputSegundaFase();
        Usuario usuario = new Usuario();
        Mockito.when(usuarioService.cadastrarSegundaFase(eq(id), any(UsuarioInputSegundaFase.class))).thenReturn(usuario);

        ResponseEntity<Usuario> response = usuarioController.completarCadastroUsuario(id, usuarioInputSegundaFase);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuario, response.getBody());
    }

    @Test
    public void testAtualizarUsuario() {        Integer id = 1;
        UsuarioInputSegundaFase usuarioInputSegundaFase = new UsuarioInputSegundaFase();
        UsuarioListarOutput usuarioAtualizado = new UsuarioListarOutput();
        Mockito.when(usuarioService.atualizarUsuario(eq(id), any(UsuarioInputSegundaFase.class))).thenReturn(usuarioAtualizado);

        ResponseEntity<UsuarioListarOutput> response = usuarioController.atualizarUsuario(id, usuarioInputSegundaFase);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(usuarioAtualizado, response.getBody());
    }

    @Test
    public void testAtualizarUsuarioNaoEncontrado() {        Integer id = 1;
        UsuarioInputSegundaFase usuarioInputSegundaFase = new UsuarioInputSegundaFase();
        Mockito.when(usuarioService.atualizarUsuario(eq(id), any(UsuarioInputSegundaFase.class))).thenReturn(null);

        ResponseEntity<UsuarioListarOutput> response = usuarioController.atualizarUsuario(id, usuarioInputSegundaFase);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testExcluirUsuario() {        Integer id = 1;
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

//    @Test
//    public void testCadastrarVoluntario() {
//        UsuarioInputPrimeiraFase usuarioInputPrimeiraFase = new UsuarioInputPrimeiraFase();
//        Usuario usuario = new Usuario();
//        Mockito.when(usuarioService.cadastrarPrimeiraFase(any(UsuarioInputPrimeiraFase.class))).thenReturn(usuario);
//
//        ResponseEntity<Usuario> response = usuarioController.cadastrarVoluntarioFase1(usuarioInputPrimeiraFase);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals(usuario, response.getBody());
//    }

    @Test
    public void testCompletarCadastroVoluntario() {        Integer id = 1;
        UsuarioInputSegundaFase usuarioInputSegundaFase = new UsuarioInputSegundaFase();
        Usuario usuario = new Usuario();
        Mockito.when(usuarioService.cadastrarSegundaFaseVoluntario(eq(id), any(UsuarioInputSegundaFase.class))).thenReturn(usuario);

        ResponseEntity<Usuario> response = usuarioController.completarCadastroVoluntario(id, usuarioInputSegundaFase);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuario, response.getBody());
    }

}