package org.com.imaapi.integracao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class integracaoInfinitePay {

    @Value("${infinitepay.api.key}")
    private String apiKey;

    @Value("${infinitepay.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> criarCobranca(String payloadJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(payloadJson, headers);

        return restTemplate.exchange(
                baseUrl + "/charges",
                HttpMethod.POST,
                entity,
                String.class
        );
    }
}