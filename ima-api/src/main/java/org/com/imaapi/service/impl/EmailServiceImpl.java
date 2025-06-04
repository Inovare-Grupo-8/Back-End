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
            switch (assunto) {                case "continuar cadastro":
                    logger.info("Enviando email para continuar cadastro");
                    helper.setSubject("Complete seu cadastro no IMA!");
                    String[] parts = nome.split("\\|");
                    String nomeUsuario = parts[0];
                    Integer idUsuario = Integer.parseInt(parts[1]);
                    htmlContent = gerarConteudoHtmlContinuarCadastro(nomeUsuario, idUsuario);
                    break;
                case "bem vindo":
                    logger.info("Enviando email de boas-vindas - usuário comum");
                    helper.setSubject("Bem-vindo ao IMA!");
                    htmlContent = gerarConteudoHtmlBemVindo(nome);
                    break;
                case "bem vindo voluntario":
                    logger.info("Enviando email de boas-vindas - voluntário");
                    helper.setSubject("Seja bem-vindo ao IMA Voluntário!");
                    htmlContent = gerarConteudoHtmlBemVindoVoluntario(nome);
                    break;
                case "agendamento realizado":
                    logger.info("Enviando email agendamento de voluntario");
                    helper.setSubject("Agendamento Realizado");
                    htmlContent = gerarConteudoHtmlAgendamento(nome, "Agendamento Realizado");
                    break;
                case "agendamento cancelado":
                    logger.info("Enviando email agendamento cancelado");
                    helper.setSubject("Agendamento Cancelado");
                    htmlContent = gerarConteudoHtmlAgendamento(nome, "Agendamento Cancelado");
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
    }    private String gerarConteudoHtmlContinuarCadastro(String nome, Integer idUsuario) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Complete seu cadastro no IMA!</title>
                </head>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0;">
                    <div style="max-width: 600px; margin: 20px auto; background: #ffffff; padding: 40px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);">
                        <div style="text-align: center; margin-bottom: 30px;">
                            <img src="https://i.ibb.co/MDHNc40s/logo-v2.png" alt="Logo IMA" style="width: 120px; height: auto;">
                        </div>
                        <div style="margin: 20px 0; text-align: center;">
                            <h1 style="color: #8B008B; font-size: 28px; margin-bottom: 30px;">Complete seu cadastro!</h1>
                            <p style="color: #333333; font-size: 16px; line-height: 1.6;">Olá, <strong>%s</strong>,</p>
                            <p style="color: #333333; font-size: 16px; line-height: 1.6; margin: 20px 0;">Estamos quase lá! Para aproveitar todos os nossos serviços, precisamos que você complete seu cadastro.</p>                            
                            <p style="color: #333333; font-size: 16px; line-height: 1.6;">É rápido e simples, basta clicar no botão abaixo:</p>
                            <a href="http://localhost:3030/inscricao-anamnese?id=%d"
                               style="display: inline-block; margin: 30px 0; padding: 12px 40px; background-color: #FF4040; color: white; text-decoration: none; font-weight: bold; border-radius: 25px; font-size: 16px;">Completar Cadastro</a>
                        </div>
                        <div style="text-align: center; font-size: 14px; color: #666; margin-top: 40px; padding-top: 20px; border-top: 1px solid #eee;">
                            <p>&copy; 2025 IMA. Todos os direitos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(nome, idUsuario);
    }    private String gerarConteudoHtmlBemVindo(String nome) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Bem-vindo ao IMA!</title>
                </head>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0;">
                    <div style="max-width: 600px; margin: 20px auto; background: #ffffff; padding: 40px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);">
                        <div style="text-align: center; margin-bottom: 30px;">
                            <img src="https://i.ibb.co/MDHNc40s/logo-v2.png" alt="Logo IMA" style="width: 120px; height: auto;">
                        </div>
                        <div style="margin: 20px 0; text-align: center;">
                            <h1 style="color: #8B008B; font-size: 28px; margin-bottom: 30px;">É uma alegria ter você com a gente!</h1>
                            <p style="color: #333333; font-size: 16px; line-height: 1.6;">Olá, <strong>%s</strong>,</p>
                            <p style="color: #333333; font-size: 16px; line-height: 1.6; margin: 20px 0;">Seu cadastro foi realizado com sucesso e agora você já pode agendar os serviços disponíveis pela IMA.</p>
                            <p style="color: #333333; font-size: 16px; line-height: 1.6;">Como organização social, nosso propósito é oferecer apoio e cuidado a quem precisa, com atendimentos acessíveis, de qualidade e conduzidos com respeito e acolhimento.</p>
                            <p style="color: #333333; font-size: 16px; line-height: 1.6;">Você encontrará na nossa plataforma serviços pensados com carinho para ajudar em diferentes momentos da sua vida.</p>
                            <p style="color: #333333; font-size: 16px; line-height: 1.6; margin: 20px 0;">Pronto(a) para começar? É só acessar a plataforma:</p>
                            <a href="http://localhost:3030/login"
                               style="display: inline-block; margin: 30px 0; padding: 12px 40px; background-color: #FF4040; color: white; text-decoration: none; font-weight: bold; border-radius: 25px; font-size: 16px;">Acessar Plataforma</a>
                        </div>
                        <div style="text-align: center; font-size: 14px; color: #666; margin-top: 40px; padding-top: 20px; border-top: 1px solid #eee;">
                            <p>&copy; 2025 IMA. Todos os direitos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(nome);
    }    private String gerarConteudoHtmlBemVindoVoluntario(String nome) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Bem-vindo ao IMA Voluntário!</title>
                </head>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0;">
                    <div style="max-width: 600px; margin: 20px auto; background: #ffffff; padding: 40px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);">
                        <div style="text-align: center; margin-bottom: 30px;">
                            <img src="https://i.ibb.co/MDHNc40s/logo-v2.png" alt="Logo IMA" style="width: 120px; height: auto;">
                        </div>
                        <div style="margin: 20px 0; text-align: center;">
                            <h1 style="color: #8B008B; font-size: 28px; margin-bottom: 30px;">É uma alegria ter você com a gente!</h1>
                            <p style="color: #333333; font-size: 16px; line-height: 1.6;">Olá, <strong>%s</strong>,</p>
                            <p style="color: #333333; font-size: 16px; line-height: 1.6; margin: 20px 0;">Estamos muito felizes em ter você como voluntário no IMA! Seu cadastro foi concluído com sucesso.</p>
                            <p style="color: #333333; font-size: 16px; line-height: 1.6;">Sua dedicação e compromisso farão a diferença na vida de muitas pessoas.</p>
                            <p style="color: #333333; font-size: 16px; line-height: 1.6;">Para começar a ajudar, acesse a plataforma e gerencie seus horários e atendimentos:</p>
                            <a href="http://localhost:3030/login"
                               style="display: inline-block; margin: 30px 0; padding: 12px 40px; background-color: #FF4040; color: white; text-decoration: none; font-weight: bold; border-radius: 25px; font-size: 16px;">Acessar Plataforma</a>
                        </div>
                        <div style="text-align: center; font-size: 14px; color: #666; margin-top: 40px; padding-top: 20px; border-top: 1px solid #eee;">
                            <p>&copy; 2025 IMA. Todos os direitos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(nome);
    }    private String gerarConteudoHtmlAgendamento(String nome, String assunto) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                </head>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0;">
                    <div style="max-width: 600px; margin: 20px auto; background: #ffffff; padding: 40px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);">
                        <div style="text-align: center; margin-bottom: 30px;">
                            <img src="https://i.ibb.co/MDHNc40s/logo-v2.png" alt="Logo IMA" style="width: 120px; height: auto;">
                        </div>
                        <div style="margin: 20px 0; text-align: center;">
                            <h1 style="color: #8B008B; font-size: 28px; margin-bottom: 30px;">Recebemos seu agendamento e já está tudo certo!</h1>
                            <p style="color: #333333; font-size: 16px; line-height: 1.6;">Olá, <strong>%s</strong>,</p>
                            <p style="color: #333333; font-size: 16px; line-height: 1.6; margin: 20px 0;">Agradecemos pela confiança em contar com os serviços da IMA.</p>
                            <p style="color: #333333; font-size: 16px; line-height: 1.6;">Você receberá um lembrete próximo ao dia agendado para que não perca o atendimento.</p>
                            <p style="color: #333333; font-size: 16px; line-height: 1.6; margin: 20px 0;">Caso precise remarcar ou tirar dúvidas, estamos à disposição.</p>
                            <a href="http://localhost:3030/login"
                               style="display: inline-block; margin: 30px 0; padding: 12px 40px; background-color: #FF4040; color: white; text-decoration: none; font-weight: bold; border-radius: 25px; font-size: 16px;">Acessar Plataforma</a>
                        </div>
                        <div style="text-align: center; font-size: 14px; color: #666; margin-top: 40px; padding-top: 20px; border-top: 1px solid #eee;">
                            <p>&copy; 2025 IMA. Todos os direitos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(assunto, nome);
    }
}