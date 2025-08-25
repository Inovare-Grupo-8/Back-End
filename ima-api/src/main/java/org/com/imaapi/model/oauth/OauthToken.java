package org.com.imaapi.model.oauth;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.com.imaapi.model.usuario.Usuario;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "oauth_token")
@Data
@Getter
@Setter
public class OauthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_oauth_token")
    private Integer id;

    @OneToOne
    @JoinColumn(
            name = "fk_usuario",
            foreignKey = @ForeignKey(name = "fk_token_usuario")
    )
    private Usuario usuario;

    @Column(length = 2048)
    private String accessTokenValue;

    private Instant accessTokenIssuedAt;
    private Instant accessTokenExpiresAt;

    @Column(length = 512)
    private String refreshTokenValue;
    private Instant refreshTokenIssuedAt;

    @Column(length = 2048)
    private String scopes;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Version
    @Column(name = "versao")
    private Integer versao = 0;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (criadoEm == null) {
            criadoEm = now;
        }
        if (atualizadoEm == null) {
            atualizadoEm = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public void atualizarTokens(OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken) {
        this.accessTokenValue = accessToken.getTokenValue();
        this.accessTokenIssuedAt = accessToken.getIssuedAt();
        this.accessTokenExpiresAt = accessToken.getExpiresAt();
        this.scopes = String.join(",", accessToken.getScopes());

        if (refreshToken != null) {
            this.refreshTokenValue = refreshToken.getTokenValue();
            this.refreshTokenIssuedAt = refreshToken.getIssuedAt();
        }
    }

    public OAuth2AccessToken getAccessTokenObject() {
        return new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                accessTokenValue,
                accessTokenIssuedAt,
                accessTokenExpiresAt,
                scopes != null ? Set.of(scopes.split(",")) : Set.of()
        );
    }

    public OAuth2RefreshToken getRefreshTokenObject() {
        return refreshTokenValue != null ?
                new OAuth2RefreshToken(refreshTokenValue, refreshTokenIssuedAt) :
                null;
    }
}
