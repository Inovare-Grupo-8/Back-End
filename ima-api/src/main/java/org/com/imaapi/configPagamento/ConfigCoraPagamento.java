package org.com.imaapi.configPagamento;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Getter
@Setter
@Configuration
public class ConfigCoraPagamento {



        @Value("${cora.api.base-url}")
        private String baseUrl;

        @Value("${cora.api.client-id}")
        private String clientId;

        @Value("${cora.api.client-secret}")
        private String clientSecret;

        public String getBaseUrl() {
            return baseUrl;
        }

        public String getClientId() {
            return clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }
    }







