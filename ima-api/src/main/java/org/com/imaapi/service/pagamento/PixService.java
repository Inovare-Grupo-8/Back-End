package org.com.imaapi.service.pagamento;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.com.imaapi.config.ConfigCoraPagamento;
import org.com.imaapi.model.pagamento.dto.Charge;
import org.com.imaapi.model.pagamento.dto.PixPaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class PixService {

    @Autowired
    private CoraService coraService;

    @Autowired
    private ConfigCoraPagamento config;

    public PixPaymentResponse realizarPagamentoPix(Charge charge) throws Exception {
        String accessToken = coraService.obterToken();

        URL url = new URL(config.getBaseUrl() + "/charges");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setDoOutput(true);

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequest = mapper.writeValueAsString(charge);

        OutputStream os = conn.getOutputStream();
        os.write(jsonRequest.getBytes());
        os.flush();

        if (conn.getResponseCode() != 201) {
            throw new RuntimeException("Erro ao realizar pagamento Pix. Código: " + conn.getResponseCode());
        }

        PixPaymentResponse response = mapper.readValue(conn.getInputStream(), PixPaymentResponse.class);
        return response;
    }

    public String gerarCobrancaPix(Charge charge) {
        return "Gerando cobrança pix";
    }
}
