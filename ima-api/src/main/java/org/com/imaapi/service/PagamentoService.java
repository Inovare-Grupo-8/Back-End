package org.com.imaapi.service;

import org.com.imaapi.config.MercadoPagoConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MercadoPagoService {

    private static final String BASE_URL = "https://api.mercadopago.com/v1/payments";  // URL da API de pagamentos do Mercado Pago

    private final RestTemplate restTemplate;

    public MercadoPagoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String realizarPagamento(String dadosPagamento) {
        // Definir o cabeçalho com o token de acesso
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + MercadoPagoConfig.ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Corpo da requisição (dados do pagamento)
        String jsonBody = "{...}"; // Substitua com os dados do pagamento no formato JSON

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        // Enviar a requisição POST para o Mercado Pago
        ResponseEntity<String> response = restTemplate.exchange(BASE_URL, HttpMethod.POST, entity, String.class);

        // Verificar a resposta
        if (response.getStatusCode() == HttpStatus.CREATED) {
            return response.getBody();  // Retorna a resposta do pagamento
        } else {
            return "Erro ao processar o pagamento";
        }
    }
}
