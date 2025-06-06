package org.com.imaapi.config.oauth2;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OAuth2ClientConfig {

    private OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> refreshTokenTokenResponseClient(WebClient webClient) {
        return new SpringSecurityOAuth2RefreshTokenResponseClient(webClient);
    }

    private record SpringSecurityOAuth2RefreshTokenResponseClient(WebClient webClient)
            implements OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> {

        @Override
        public OAuth2AccessTokenResponse getTokenResponse(OAuth2RefreshTokenGrantRequest refreshTokenGrantRequest) {
            ClientRegistration clientRegistration = refreshTokenGrantRequest.getClientRegistration();

            return webClient.post()
                    .uri(clientRegistration.getProviderDetails().getTokenUri())
                    .body(BodyInserters.fromFormData(createRequestParameters(refreshTokenGrantRequest)))
                    .headers(headers -> headers.setBasicAuth(clientRegistration.getClientId(), clientRegistration.getClientSecret()))
                    .retrieve()
                    .bodyToMono(OAuth2AccessTokenResponse.class)
                    .block();
        }

        private MultiValueMap<String, String> createRequestParameters(OAuth2RefreshTokenGrantRequest refreshTokenGrantRequest) {
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add(OAuth2ParameterNames.GRANT_TYPE, refreshTokenGrantRequest.getGrantType().getValue());
            parameters.add(OAuth2ParameterNames.REFRESH_TOKEN, refreshTokenGrantRequest.getRefreshToken().getTokenValue());
            parameters.add(OAuth2ParameterNames.SCOPE, String.join(" ", refreshTokenGrantRequest.getClientRegistration().getScopes()));
            return parameters;
        }
    }
}
