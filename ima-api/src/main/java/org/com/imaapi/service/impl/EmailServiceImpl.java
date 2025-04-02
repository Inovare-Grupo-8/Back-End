package org.com.imaapi.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.com.imaapi.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    // Logger para registrar informações e erros
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    // Injeção do JavaMailSender para envio de e-mails
    @Autowired
    private JavaMailSender javaMailSender;

    // Valor do remetente configurado no arquivo application.properties
    @Value("${spring.mail.username}")
    private String remetente;

    /* Método para enviar um e-mail de boas-vindas ao usuário. */
    public String enviarEmailCadastroFeito(String destinatario, String nome) {
        // Verifica se o destinatário é nulo ou vazio
        if (destinatario == null || destinatario.isEmpty()) {
            logger.error("O destinatário do e-mail está vazio ou nulo.");
            return "Erro: O destinatário do e-mail não pode ser vazio ou nulo.";
        }

        logger.info("Enviando e-mail para: {}", destinatario);
        try {
            // Cria uma mensagem de e-mail
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Configura o remetente, destinatário e assunto do e-mail
            helper.setFrom(remetente);
            helper.setTo(destinatario);
            helper.setSubject("Bem-vindo ao IMA!");

            // Gera o conteúdo HTML do e-mail
            String htmlContent = gerarConteudoHtmlCadastro(nome);
            helper.setText(htmlContent, true); // Define o conteúdo como HTML

            // Envia o e-mail
            javaMailSender.send(mimeMessage);
            logger.info("E-mail enviado com sucesso para: {}", destinatario);
            return "E-mail enviado com sucesso!";
        } catch (MessagingException e) {
            // Registra o erro caso o envio falhe
            logger.error("Erro ao enviar e-mail para {}: {}", destinatario, e.getMessage(), e);
            return "Erro ao enviar e-mail: " + e.getMessage();
        }
    }

    /* Método para gerar o conteúdo HTML do e-mail de boas-vindas. */

    private String gerarConteudoHtmlCadastro(String nome) {
        // URL pública da imagem do logo
        String logoUrl = "https://i.ibb.co/MDHNc40s/logo-v2.png"; // Substitua pelo URL público da imagem
        return """
                    <!DOCTYPE html>
                    <html lang="pt-BR">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Bem-vindo ao IMA!</title>
                    </head>
                    <body style="font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0;">
                        <div style="max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);">
                            <!-- Cabeçalho com o logo -->
                            <div style="display: flex; flex-direction: row; align-items: center; justify-content: space-between; padding: 15px; border-bottom: 2px solid #ED4231;">
                                <img src="%s" alt="Logo IMA" style="width: 20%; height: auto;">
                            </div>
                            <!-- Conteúdo principal -->
                            <div style="margin: 20px 0; text-align: center;">
                                <h1 style="color: #ED4231; font-size: 24px;">Bem-vindo ao IMA!</h1>
                                <p style="color: #262626; font-size: 16px; line-height: 1.5;">Olá, <strong>%s</strong>,</p>
                                <p style="color: #262626; font-size: 16px; line-height: 1.5;">Obrigado por se cadastrar no IMA. Estamos muito felizes em tê-lo conosco!</p>
                                <p style="color: #262626; font-size: 16px; line-height: 1.5;">Explore nossos serviços e aproveite ao máximo a experiência.</p>
                                <!-- Botão para acessar a plataforma -->
                                <a href="https://inovare-grupo-8.github.io/Site-Institucional-IMA/public/views/login-cadastro.html"
                                   style="display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #ED4231; color: #f4f4f9; text-decoration: none; font-weight: bold; border-radius: 20px; transition: background-color 0.3s ease;">Acessar Plataforma</a>
                            </div>
                            <!-- Rodapé -->
                            <div style="text-align: center; font-size: 12px; color: #888; margin-top: 20px; padding-top: 10px; border-top: 1px solid #ddd;">
                                <p>&copy; 2025 IMA. Todos os direitos reservados.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                """
                .replaceFirst("%s", logoUrl) // Substitui o primeiro %s pela URL do logo
                .replaceFirst("%s", nome); // Substitui o segundo %s pelo nome do destinatário
    }
}