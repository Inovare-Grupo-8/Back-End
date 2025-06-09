package imaapi.controller;

import org.com.imaapi.controller.ConsultaController;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.service.ConsultaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class ConsultaControllerTest {

    @InjectMocks
    private ConsultaController consultaController;

    @Mock
    private ConsultaService consultaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCriarEventoComSucesso() {
        ConsultaInput consultaInput = new ConsultaInput();
        ConsultaOutput consultaOutput = new ConsultaOutput();
        ResponseEntity<ConsultaOutput> expectedResponse = ResponseEntity.ok(consultaOutput);
        Mockito.when(consultaService.criarEvento(any(ConsultaInput.class))).thenReturn(expectedResponse);
        ResponseEntity<ConsultaOutput> response = consultaController.criarEvento(consultaInput);
        assertEquals(expectedResponse, response);
    }

    @Test
    public void testCriarEventoComErroValidacao() {
        ConsultaInput consultaInput = new ConsultaInput();
        Mockito.when(consultaService.criarEvento(any(ConsultaInput.class)))
                .thenThrow(new IllegalArgumentException("Erro de validação"));
        try {
            consultaController.criarEvento(consultaInput);
        } catch (IllegalArgumentException e) {
            assertEquals("Erro de validação", e.getMessage());
        }
    }

    @Test
    public void testCriarEventoRetornaNull() {
        ConsultaInput consultaInput = new ConsultaInput();
        Mockito.when(consultaService.criarEvento(any(ConsultaInput.class))).thenReturn(null);
        ResponseEntity<ConsultaOutput> response = consultaController.criarEvento(consultaInput);
        assertEquals(null, response);
    }

    @Test
    public void testCriarEventoComErroInterno() {
        ConsultaInput consultaInput = new ConsultaInput();
        Mockito.when(consultaService.criarEvento(any(ConsultaInput.class)))
                .thenThrow(new RuntimeException("Erro interno"));
        try {
            consultaController.criarEvento(consultaInput);
        } catch (RuntimeException e) {
            assertEquals("Erro interno", e.getMessage());
        }
    }

    @Test
    public void testCriarEventoComRespostaNaoEsperada() {
        ConsultaInput consultaInput = new ConsultaInput();
        ResponseEntity<ConsultaOutput> unexpectedResponse = ResponseEntity.badRequest().build();
        Mockito.when(consultaService.criarEvento(any(ConsultaInput.class))).thenReturn(unexpectedResponse);
        ResponseEntity<ConsultaOutput> response = consultaController.criarEvento(consultaInput);
        assertEquals(unexpectedResponse, response);
    }

    @Test
    public void testCriarEventoComInputNulo() {
        try {
            consultaController.criarEvento(null);
        } catch (IllegalArgumentException e) {
            assertEquals("ConsultaInput não pode ser nulo", e.getMessage());
        }
    }

    @Test
    public void testGetConsultasDiaComSucesso() {
        Mockito.when(consultaService.getConsultasDia(any(String.class))).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<?> response = consultaController.getConsultasDia("user");
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testGetConsultasDiaComErro() {
        Mockito.when(consultaService.getConsultasDia(any(String.class))).thenThrow(new RuntimeException("Erro ao buscar consultas"));
        try {
            consultaController.getConsultasDia("user");
        } catch (RuntimeException e) {
            assertEquals("Erro ao buscar consultas", e.getMessage());
        }
    }

    @Test
    public void testAdicionarFeedback() {
        Mockito.when(consultaService.adicionarFeedback(any(Integer.class), any(String.class))).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<?> response = consultaController.adicionarFeedback(1, "feedback");
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testAdicionarFeedbackComErro() {
        Mockito.when(consultaService.adicionarFeedback(any(Integer.class), any(String.class)))
                .thenThrow(new RuntimeException("Erro ao adicionar feedback"));
        try {
            consultaController.adicionarFeedback(1, "feedback");
        } catch (RuntimeException e) {
            assertEquals("Erro ao adicionar feedback", e.getMessage());
        }
    }

    @Test
    public void testAdicionarAvaliacaoComErro() {
        Mockito.when(consultaService.adicionarAvaliacao(any(Integer.class), any(String.class))).thenThrow(new RuntimeException("Erro ao adicionar avaliação"));
        try {
            consultaController.adicionarAvaliacao(1, "avaliacao");
        } catch (RuntimeException e) {
            assertEquals("Erro ao adicionar avaliação", e.getMessage());
        }
    }

    @Test
    public void testAdicionarAvaliacaoComSucesso() {
        Mockito.when(consultaService.adicionarAvaliacao(any(Integer.class), any(String.class)))
                .thenReturn(ResponseEntity.ok().build());
        ResponseEntity<?> response = consultaController.adicionarAvaliacao(1, "avaliacao");
        assertEquals(200, response.getStatusCodeValue());
    }
}