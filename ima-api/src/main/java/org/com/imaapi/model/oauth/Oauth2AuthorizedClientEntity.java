package org.com.imaapi.model.oauth;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "oauth2_authorized_client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Oauth2AuthorizedClientEntity {

    @EmbeddedId
    private Oauth2AuthorizedClientId id;

    @Column(name = "access_token_type", length = 50)
    private String accessTokenType;

    @Lob
    @Column(name = "access_token_value")
    private String accessTokenValue;

    @Column(name = "access_token_issued_at")
    private Instant accessTokenIssuedAt;

    @Column(name = "access_token_expires_at")
    private Instant accessTokenExpiresAt;

    @Column(name = "access_token_scopes")
    private String accessTokenScopes;

    @Lob
    @Column(name = "refresh_token_value")
    private String refreshTokenValue;

    @Column(name = "refresh_token_issued_at")
    private Instant refreshTokenIssuedAt;
}
