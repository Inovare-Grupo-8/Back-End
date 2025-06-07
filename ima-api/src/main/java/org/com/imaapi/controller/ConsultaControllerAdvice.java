package org.com.imaapi.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ConsultaControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaControllerAdvice.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.error("Erro ao converter a mensagem HTTP: {}", ex.getMessage());

        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex.getCause();

            if (ife.getCause() instanceof DateTimeParseException) {

                Map<String, Object> body = new HashMap<>();
                body.put("status", HttpStatus.BAD_REQUEST.value());
                body.put("error", "Formato de data inválido");
                body.put("message", "O formato de data deve ser 'yyyy-MM-dd HH:mm:ss' ou 'yyyy-MM-ddTHH:mm:ss'. Exemplo: '2025-06-10 14:30:00' ou '2025-06-10T14:30:00'");

                return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
            }

            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", "Erro de formato inválido");
            body.put("property", ife.getPathReference());
            body.put("value", ife.getValue());
            body.put("targetType", ife.getTargetType().getSimpleName());
            body.put("message", "O valor '" + ife.getValue() + "' não é válido para o campo '" +
                    ife.getPathReference() + "'. Esperava-se um valor do tipo " + ife.getTargetType().getSimpleName());

            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        if (ex.getMessage() != null && ex.getMessage().contains("Invalid JSON")) {
            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", "JSON inválido");
            body.put("message", "O JSON enviado está mal formatado ou incompleto. Verifique a sintaxe e tente novamente.");

            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Erro na requisição");
        body.put("message", "O formato da requisição está incorreto. Verifique os campos e tente novamente.");
        body.put("details", ex.getMessage());
        logger.debug("Detalhes do erro: {}", ex.getCause() != null ? ex.getCause().getMessage() : "Sem detalhes adicionais");

        if (ex.getMessage() != null &&
                (ex.getMessage().contains("Unexpected end of JSON input") ||
                        ex.getMessage().contains("Unexpected end of input"))) {
            body.put("error", "JSON incompleto");
            body.put("message", "O JSON enviado está incompleto. Verifique se todos os colchetes e chaves foram fechados corretamente.");
        }

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleEntityValidation(RuntimeException ex) {
        logger.error("Erro de validação de entidade: {}", ex.getMessage());

        if (ex.getMessage().contains("Especialidade") ||
                ex.getMessage().contains("Assistido") ||
                ex.getMessage().contains("Voluntário")) {

            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", "Validação de entidade falhou");
            body.put("message", ex.getMessage());

            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        throw ex;
    }
}
