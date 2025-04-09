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

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    public String enviarEmail(String destinatario, String nome, String assunto) {
        if (destinatario == null || destinatario.isEmpty()) {
            logger.error("O destinatário do e-mail está vazio ou nulo.");
            return "Erro: O destinatário do e-mail não pode ser vazio ou nulo.";
        }

        logger.info("Enviando e-mail para: {}", destinatario);
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(remetente);
            helper.setTo(destinatario);

            String htmlContent;
            switch (assunto) {
                case "cadastro de email":
                    logger.info("Enviando email de cadastro de usuario - Bem sucedido");
                    helper.setSubject("Bem-vindo ao IMA!");
                    htmlContent = gerarConteudoHtmlCadastro(nome, "Seja bem-vindo(a) ao IMA!");
                    break;
                case "cadastro de voluntario":
                    logger.info("Enviando email de cadastro de voluntário - Bem sucedido");
                    helper.setSubject("Seja bem-vindo ao IMA Voluntario!");
                    htmlContent = gerarConteudoHtmlCadastro(nome, "Seja bem-vindo(a) ao Instituto Mãos Amigas Voluntário!");
                    break;
                case "agendamento realizado":
                        logger.info("Enviando email agendamento realizado para o usuário comum");
                        helper.setSubject("Agendamento Realizado");
                        htmlContent = gerarConteudoHtmlAgendamento(nome, "Agendamento Realizado", "Seu agendamento foi realizado!");
                        helper.setText(htmlContent, true);
                        javaMailSender.send(mimeMessage);

                        logger.info("Enviando email agendamento realizado para o voluntário");
                        helper.setSubject("Agendamento Realizado");
                        htmlContent = gerarConteudoHtmlAgendamento(nome, "Agendamento Realizado", "Um agendamento foi realizado!");
                        helper.setText(htmlContent, true);
                        javaMailSender.send(mimeMessage);
                        break;
                case "agendamento cancelado":
                    logger.info("Enviando email agendamento cancelado");
                    helper.setSubject("Agendamento Cancelado");
                    htmlContent = gerarConteudoHtmlAgendamento(nome, "Agendamento Cancelado", "Seu agendamento foi cancelado com sucesso!");
                    break;
                default:
                    logger.info("Assunto não encontrado");
                    return "Erro: Assunto não encontrado.";
            }

            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
            logger.info("E-mail enviado com sucesso para: {}", destinatario);
            return "E-mail enviado com sucesso!";
        } catch (MessagingException e) {
            logger.error("Erro ao enviar e-mail para {}: {}", destinatario, e.getMessage(), e);
            return "Erro ao enviar e-mail: " + e.getMessage();
        }
    }

    private String gerarConteudoHtmlCadastro(String nome, String assunto) {
        String logoUrl = "https://i.ibb.co/MDHNc40s/logo-v2.png";
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
                        <div style="display: flex; flex-direction: row; align-items: center; justify-content: space-between; padding: 15px; border-bottom: 2px solid #ED4231;">
                            <img src="%s" alt="Logo IMA" style="width: 10%%; height: auto;">
                        </div>
                        <div style="margin: 20px 0; text-align: center;">
                            <h1 style="color: #ED4231; font-size: 24px;">%s</h1>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Olá, <strong>%s</strong>,</p>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Obrigado por se cadastrar no IMA. Estamos muito felizes em tê-lo conosco!</p>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Explore nossos serviços e aproveite ao máximo a experiência.</p>
                            <a href="https://inovare-grupo-8.github.io/Site-Institucional-IMA/public/views/login-cadastro.html"
                               style="display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #ED4231; color: #f4f4f9; text-decoration: none; font-weight: bold; border-radius: 20px; transition: background-color 0.3s ease;">Acessar Plataforma</a>
                        </div>
                        <div style="text-align: center; font-size: 12px; color: #888; margin-top: 20px; padding-top: 10px; border-top: 1px solid #ddd;">
                            <p>&copy; 2025 IMA. Todos os direitos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .replaceFirst("%s", "")
                .replaceFirst("%s", assunto)
                .replaceFirst("%s", nome);
    }

    private String gerarConteudoHtmlAgendamento(String nome, String assunto, String mensagem) {
        String logoUrl = "https://i.ibb.co/MDHNc40s/logo-v2.png";
        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Agendamento Realizado - IMA</title>
            </head>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0;">
                <div style="max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);">
                    <div style="display: flex; flex-direction: row; align-items: center; justify-content: space-between; padding: 15px; border-bottom: 2px solid #ED4231;">
                        <img src="%s" alt="Logo IMA" style="width: 10%%; height: auto;">
                    </div>
                    <div style="margin: 20px 0; text-align: center;">
                        <h1 style="color: #ED4231; font-size: 24px;">%s</h1>
                        <p style="color: #262626; font-size: 16px; line-height: 1.5;">Olá, <strong>%s</strong>,</p>
                        <p style="color: #262626; font-size: 16px; line-height: 1.5;">%s</p>
                        <p style="color: #262626; font-size: 16px; line-height: 1.5;">Você pode acessar a plataforma para ver os detalhes do seu agendamento.</p>
                        <a href="https://inovare-grupo-8.github.io/Site-Institucional-IMA/public/views/login-cadastro.html"
                           style="display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #ED4231; color: #ffffff; text-decoration: none; font-weight: bold; border-radius: 20px; transition: background-color 0.3s ease;">
                            Ver Detalhes
                        </a>
                    </div>
                    <div style="text-align: center; font-size: 12px; color: #888; margin-top: 20px; padding-top: 10px; border-top: 1px solid #ddd;">
                        <p>&copy; 2025 IMA. Todos os direitos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """
                .replaceFirst("%s", logoUrl)
                .replaceFirst("%s", assunto)
                .replaceFirst("%s", nome)
                .replaceFirst("%s", mensagem);
    }

}