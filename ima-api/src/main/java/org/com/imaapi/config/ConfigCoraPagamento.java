package org.com.imaapi.config;

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
}







