package org.com.imaapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to help with JSON validation and diagnostics
 */
public class JsonValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonValidator.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * Validates if a string is a well-formed JSON
     * @param json The JSON string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidJson(String json) {
        try {
            mapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            logger.error("JSON inválido: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets detailed error information about JSON parsing issues
     * @param json The JSON string to validate
     * @return Error message or "Valid JSON" if valid
     */
    public static String getJsonErrors(String json) {
        try {
            mapper.readTree(json);
            return "JSON válido";
        } catch (JsonProcessingException e) {
            return formatJsonError(json, e);
        }
    }
    
    /**
     * Formats a JSON error in a readable way
     */
    private static String formatJsonError(String json, JsonProcessingException e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Erro ao processar JSON:\n");
        sb.append(e.getMessage()).append("\n\n");
        
        // For specific error: Unexpected end of input
        if (e.getMessage().contains("Unexpected end of input")) {
            sb.append("JSON incompleto. Verifique se todas as chaves e colchetes foram fechados corretamente.\n");
        }
        
        // Get line and column from error
        int line = e.getLocation().getLineNr();
        int column = e.getLocation().getColumnNr();
        
        sb.append("Posição do erro: linha ").append(line)
          .append(", coluna ").append(column).append("\n");
          
        return sb.toString();
    }
}
