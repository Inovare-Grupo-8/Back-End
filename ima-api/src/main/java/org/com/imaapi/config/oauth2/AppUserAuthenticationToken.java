package org.com.imaapi.config.oauth2;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

import java.util.Collection;

@Getter
public class AppUserAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private Object credentials;
    private String provider;
    private OAuth2AuthorizedClient authorizedClient;

    public AppUserAuthenticationToken(Object principal,
                                      Object credentials,
                                      Collection<? extends GrantedAuthority> authorities,
                                      String provider,
                                      OAuth2AuthorizedClient authorizedClient) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.provider = provider;
        this.authorizedClient = authorizedClient;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public boolean isGoogleUser() {
        return "google".equals(provider) && authorizedClient != null;
    }
}
