package org.com.imaapi.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class JacksonConfig {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String ALT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final ZoneId DEFAULT_TIMEZONE = ZoneId.of("America/Sao_Paulo");

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();

        SimpleModule dateModule = new SimpleModule();
        dateModule.addDeserializer(LocalDateTime.class, new FlexibleLocalDateTimeDeserializer());

        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(
                DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        javaTimeModule.addSerializer(LocalDateTime.class, localDateTimeSerializer);

        javaTimeModule.addDeserializer(LocalDateTime.class, new FlexibleLocalDateTimeDeserializer());

        mapper.registerModule(javaTimeModule);
        mapper.registerModule(dateModule);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }

    public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FlexibleLocalDateTimeDeserializer.class);

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateString = p.getText();

            if (dateString == null || dateString.trim().isEmpty()) {
                logger.error("Data fornecida está vazia ou nula");
                throw new IOException("A data e hora não podem estar vazias");
            }

            logger.debug("Tentando deserializar data: {}", dateString);
            try {
                LocalDateTime dt = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(ALT_DATE_TIME_FORMAT));
                return dt.atZone(DEFAULT_TIMEZONE).toLocalDateTime();
            } catch (DateTimeParseException e1) {
                logger.debug("Formato com espaço falhou: {}", e1.getMessage());

                try {
                    LocalDateTime dt = LocalDateTime.parse(dateString);
                    return dt.atZone(DEFAULT_TIMEZONE).toLocalDateTime();
                } catch (DateTimeParseException e2) {
                    logger.debug("Formato ISO falhou: {}", e2.getMessage());

                    try {
                        return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
                    } catch (DateTimeParseException e3) {
                        logger.debug("Formato com T falhou: {}", e3.getMessage());

                        try {
                            return LocalDateTime.parse(dateString.replace(" ", "T"));
                        } catch (Exception e4) {
                            logger.error("Todos os formatos de data falharam para: {}", dateString);
                            throw new IOException("Não foi possível interpretar a data: " + dateString +
                                    ". Formatos suportados: 'yyyy-MM-dd HH:mm:ss' ou 'yyyy-MM-ddTHH:mm:ss'. " +
                                    "Exemplo: '2025-06-10 14:30:00' ou '2025-06-10T14:30:00'", e1);
                        }
                    }
                }
            }
        }
    }
}
