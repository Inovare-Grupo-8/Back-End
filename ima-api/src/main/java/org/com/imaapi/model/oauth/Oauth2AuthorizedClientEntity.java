package org.com.imaapi.model.oauth;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "access_token_value")
    private String accessTokenValue;

    @Column(name = "access_token_issued_at")
    private Instant accessTokenIssuedAt;

    @Column(name = "access_token_expires_at")
    private Instant accessTokenExpiresAt;

    @Lob
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "access_token_scopes")
    private String accessTokenScopes;

    @Lob
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "refresh_token_value")
    private String refreshTokenValue;

    @Column(name = "refresh_token_issued_at")
    private Instant refreshTokenIssuedAt;
}
