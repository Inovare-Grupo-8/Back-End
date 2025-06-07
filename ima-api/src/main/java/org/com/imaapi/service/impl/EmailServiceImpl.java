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
    private String remetente;    public String enviarEmail(String destinatario, String nome, String assunto) {
        if (destinatario == null || destinatario.isEmpty()) {
            logger.error("O destinatário do e-mail está vazio ou nulo.");
            return "Erro: O destinatário do e-mail não pode ser vazio ou nulo.";
        }

        logger.info("Enviando e-mail para: {}", destinatario);
        
        int maxTentativas = 3;
        Exception ultimaExcecao = null;
        
        for (int tentativa = 1; tentativa <= maxTentativas; tentativa++) {
            try {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                helper.setFrom(remetente);
                helper.setTo(destinatario);

                String htmlContent;
                switch (assunto) {
                    case "continuar cadastro":
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
                    case "credenciais voluntario":
                        logger.info("Enviando credenciais para voluntário");
                        helper.setSubject("Suas credenciais de acesso - IMA Voluntário");
                        String[] credenciais = nome.split("\\|");
                        String nomeVoluntario = credenciais[0];
                        String emailVoluntario = credenciais[1];
                        String senhaVoluntario = credenciais[2];
                        htmlContent = gerarConteudoHtmlCredenciaisVoluntario(nomeVoluntario, emailVoluntario, senhaVoluntario);
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
                logger.info("E-mail enviado com sucesso para: {} na tentativa {}", destinatario, tentativa);
                return "E-mail enviado com sucesso!";
                
            } catch (MessagingException e) {
                ultimaExcecao = e;
                logger.warn("Tentativa {} falhou ao enviar e-mail para {}: {}", tentativa, destinatario, e.getMessage());
                
                if (tentativa < maxTentativas) {
                    try {
                        // Aguardar antes da próxima tentativa
                        Thread.sleep(2000 * tentativa); // 2s, 4s, 6s...
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        logger.error("Thread interrompida durante espera entre tentativas");
                        break;
                    }
                }
            }
        }
        
        logger.error("Falha ao enviar e-mail para {} após {} tentativas: {}", 
                destinatario, maxTentativas, ultimaExcecao != null ? ultimaExcecao.getMessage() : "Erro desconhecido");
        return "Erro ao enviar e-mail após " + maxTentativas + " tentativas: " + 
               (ultimaExcecao != null ? ultimaExcecao.getMessage() : "Erro desconhecido");

    }

    private String gerarConteudoHtmlContinuarCadastro(String nome, Integer idUsuario) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Complete seu cadastro no IMA!</title>
                </head>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0;">
                    <div style="max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);">
                        <div style="display: flex; flex-direction: row; align-items: center; justify-content: space-between; padding: 15px; border-bottom: 2px solid #ED4231;">
                            <img src="https://i.ibb.co/MDHNc40s/logo-v2.png" alt="Logo IMA" style="width: 10%%; height: auto;">
                        </div>
                        <div style="margin: 20px 0; text-align: center;">
                            <h1 style="color: #ED4231; font-size: 24px;">Complete seu cadastro!</h1>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Olá, <strong>%s</strong>,</p>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Estamos quase lá! Para aproveitar todos os nossos serviços, precisamos que você complete seu cadastro.</p>                            
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">É rápido e simples, basta clicar no botão abaixo:</p>
                            <a href="http://localhost:3030/completar-cadastro-usuario?id=%d"
                               style="display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #ED4231; color: #f4f4f9; text-decoration: none; font-weight: bold; border-radius: 20px; transition: background-color 0.3s ease;">Completar Cadastro</a>
                        </div>
                        <div style="text-align: center; font-size: 12px; color: #888; margin-top: 20px; padding-top: 10px; border-top: 1px solid #ddd;">
                            <p>&copy; 2025 IMA. Todos os direitos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(nome, idUsuario);
    }

    private String gerarConteudoHtmlBemVindo(String nome) {
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
                            <img src="https://i.ibb.co/MDHNc40s/logo-v2.png" alt="Logo IMA" style="width: 10%%; height: auto;">
                        </div>
                        <div style="margin: 20px 0; text-align: center;">
                            <h1 style="color: #ED4231; font-size: 24px;">Bem-vindo ao IMA!</h1>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Olá, <strong>%s</strong>,</p>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Estamos muito felizes em ter você conosco! Seu cadastro foi concluído com sucesso.</p>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Agora você pode acessar todos os nossos serviços e agendar suas consultas.</p>
                            <a href="http://localhost:3030/login"
                               style="display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #ED4231; color: #f4f4f9; text-decoration: none; font-weight: bold; border-radius: 20px; transition: background-color 0.3s ease;">Acessar Plataforma</a>
                        </div>
                        <div style="text-align: center; font-size: 12px; color: #888; margin-top: 20px; padding-top: 10px; border-top: 1px solid #ddd;">
                            <p>&copy; 2025 IMA. Todos os direitos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(nome);
    }

    private String gerarConteudoHtmlBemVindoVoluntario(String nome) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Bem-vindo ao IMA Voluntário!</title>
                </head>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0;">
                    <div style="max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);">
                        <div style="display: flex; flex-direction: row; align-items: center; justify-content: space-between; padding: 15px; border-bottom: 2px solid #ED4231;">
                            <img src="https://i.ibb.co/MDHNc40s/logo-v2.png" alt="Logo IMA" style="width: 10%%; height: auto;">
                        </div>
                        <div style="margin: 20px 0; text-align: center;">
                            <h1 style="color: #ED4231; font-size: 24px;">Bem-vindo ao IMA Voluntário!</h1>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Olá, <strong>%s</strong>,</p>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Estamos muito felizes em ter você como voluntário no IMA! Seu cadastro foi concluído com sucesso.</p>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Sua dedicação e compromisso farão a diferença na vida de muitas pessoas.</p>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Acesse a plataforma para gerenciar seus horários e atendimentos:</p>
                            <a href="http://localhost:3030/login"
                               style="display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #ED4231; color: #f4f4f9; text-decoration: none; font-weight: bold; border-radius: 20px; transition: background-color 0.3s ease;">Acessar Plataforma</a>
                        </div>
                        <div style="text-align: center; font-size: 12px; color: #888; margin-top: 20px; padding-top: 10px; border-top: 1px solid #ddd;">
                            <p>&copy; 2025 IMA. Todos os direitos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(nome);
    }

    private String gerarConteudoHtmlAgendamento(String nome, String assunto) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                </head>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0;">
                    <div style="max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);">
                        <div style="display: flex; flex-direction: row; align-items: center; justify-content: space-between; padding: 15px; border-bottom: 2px solid #ED4231;">
                            <img src="https://i.ibb.co/MDHNc40s/logo-v2.png" alt="Logo IMA" style="width: 10%%; height: auto;">
                        </div>
                        <div style="margin: 20px 0; text-align: center;">
                            <h1 style="color: #ED4231; font-size: 24px;">%s</h1>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Olá, <strong>%s</strong>,</p>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Seu agendamento foi %s com sucesso!</p>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Estamos ansiosos para vê-lo em nossa instituição.</p>
                            <a href="http://localhost:3030/login"
                               style="display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #ED4231; color: #f4f4f9; text-decoration: none; font-weight: bold; border-radius: 20px; transition: background-color 0.3s ease;">Acessar Plataforma</a>
                        </div>
                        <div style="text-align: center; font-size: 12px; color: #888; margin-top: 20px; padding-top: 10px; border-top: 1px solid #ddd;">
                            <p>&copy; 2025 IMA. Todos os direitos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>                """.formatted(assunto, assunto, nome, (assunto.equals("Agendamento Cancelado") ? "cancelado" : "realizado"));
    }

    private String gerarConteudoHtmlCredenciaisVoluntario(String nome, String email, String senha) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Suas credenciais de acesso - IMA Voluntário</title>
                </head>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0;">
                    <div style="max-width: 600px; margin: 20px auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);">
                        <div style="display: flex; flex-direction: row; align-items: center; justify-content: space-between; padding: 15px; border-bottom: 2px solid #ED4231;">
                            <img src="https://i.ibb.co/MDHNc40s/logo-v2.png" alt="Logo IMA" style="width: 10%%; height: auto;">
                        </div>
                        <div style="margin: 20px 0; text-align: center;">
                            <h1 style="color: #ED4231; font-size: 24px;">Suas credenciais de acesso</h1>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Olá, <strong>%s</strong>,</p>
                            <p style="color: #262626; font-size: 16px; line-height: 1.5;">Aqui estão suas credenciais para acessar a plataforma do IMA:</p>
                            
                            <div style="background-color: #f8f9fa; border: 1px solid #dee2e6; border-radius: 8px; padding: 20px; margin: 20px 0; text-align: left;">
                                <p style="color: #262626; font-size: 16px; margin: 8px 0;"><strong>Email:</strong> %s</p>
                                <p style="color: #262626; font-size: 16px; margin: 8px 0;"><strong>Senha:</strong> %s</p>
                            </div>
                            
                            <p style="color: #dc3545; font-size: 14px; line-height: 1.5; margin: 20px 0;">
                                <strong>⚠️ Importante:</strong> Por motivos de segurança, recomendamos que você altere sua senha após o primeiro acesso.
                            </p>
                            
                            <a href="http://localhost:3030/login"
                               style="display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #ED4231; color: #f4f4f9; text-decoration: none; font-weight: bold; border-radius: 20px; transition: background-color 0.3s ease;">Acessar Plataforma</a>
                        </div>
                        <div style="text-align: center; font-size: 12px; color: #888; margin-top: 20px; padding-top: 10px; border-top: 1px solid #ddd;">
                            <p>&copy; 2025 IMA. Todos os direitos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(nome, email, senha);
    }
}