package imaapi.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.com.imaapi.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        java.lang.reflect.Field field = emailService.getClass().getDeclaredField("remetente");
        field.setAccessible(true);
        field.set(emailService, "remetente@teste.com");
    }
@Test
    public void testEnviarEmailComSucesso() {
        // Arrange
        String destinatario = "julia@teste.com";
        String nome = "Julia";
        String assunto = "continuar cadastro";

        // Act
        String resultado = emailService.enviarEmail(destinatario, nome, assunto);

        // Assert
        assertEquals("E-mail enviado com sucesso!", resultado);
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void testEnviarEmailDestinatarioVazio() {
        // Arrange
        String destinatario = "";
        String nome = "Julia";
        String assunto = "cadastro de email";

        // Act
        String resultado = emailService.enviarEmail(destinatario, nome, assunto);

        // Assert
        assertTrue(resultado.contains("Erro: O destinatário do e-mail não pode ser vazio ou nulo."));
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    public void testEnviarEmailAssuntoInvalido() {
        // Arrange
        String destinatario = "destino@teste.com";
        String nome = "Fulano";
        String assunto = "assunto desconhecido";

        // Act
        String resultado = emailService.enviarEmail(destinatario, nome, assunto);

        // Assert
        assertEquals("Erro: Assunto não encontrado.", resultado);
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }
}
