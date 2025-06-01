package org.com.imaapi.service.pagamento;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.com.imaapi.config.ConfigCoraPagamento;
import org.com.imaapi.model.pagamento.dto.TEDPaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class TEDService {

    @Autowired
    private ConfigCoraPagamento config;

    public String realizarTed(TEDPaymentResponse request, String token) throws Exception {
        URL url = new URL(config.getBaseUrl() + "/v2/transfers");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setDoOutput(true);

        String json = new ObjectMapper().writeValueAsString(request);
        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes());
        os.flush();

        if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {
            throw new RuntimeException("Erro ao realizar TED. Código: " + conn.getResponseCode());
        }

        return "TED realizada com sucesso!";
    }
}