package org.com.imaapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResponse {


        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("expires_in")
        private int expiresIn;

        // Getters e setters

        public String getAccessToken() {
            return accessToken;
        }
        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
        public int getExpiresIn() {
            return expiresIn;
        }
        public void setExpiresIn(int expiresIn) {
            this.expiresIn = expiresIn;
        }

}