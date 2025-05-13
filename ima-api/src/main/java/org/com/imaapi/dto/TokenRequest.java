package org.com.imaapi.dto;



public class TokenRequest {
    private String client_id;
    private String client_secret;
    private String grant_type = "client_credentials";

    public TokenRequest(String clientId, String clientSecret) {
        this.client_id = clientId;
        this.client_secret = clientSecret;
    }


    public String getClient_id() {
        return client_id;
    }
    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
    public String getClient_secret() {
        return client_secret;
    }
    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }
    public String getGrant_type() {
        return grant_type;
    }
}
