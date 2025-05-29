package org.com.imaapi.service.pagamento;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.com.imaapi.config.ConfigCoraPagamento;
import org.com.imaapi.model.pagamento.dto.TokenRequest;
import org.com.imaapi.model.pagamento.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class CoraService {

    @Autowired
    private ConfigCoraPagamento config;

    public String obterToken() throws Exception {
        URL url = new URL(config.getBaseUrl() + "/oauth/token");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        TokenRequest tokenRequest = new TokenRequest(
                config.getClientId(),
                config.getClientSecret(),
                "client_credentials"
        );

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(tokenRequest);

        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes());
        os.flush();

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Erro ao obter token. Código: " + conn.getResponseCode());
        }

        TokenResponse response = mapper.readValue(conn.getInputStream(), TokenResponse.class);

        return response.getAccessToken();
    }
}