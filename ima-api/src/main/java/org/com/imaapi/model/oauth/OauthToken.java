package org.com.imaapi.model.oauth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.com.imaapi.model.usuario.Usuario;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

import java.time.Instant;

@Entity
@Table(name = "oauth_token")
@Getter
@Setter
public class OauthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(
            name = "fk_usuario",
            foreignKey = @ForeignKey(name = "fk_token_usuario")
    )
    private Usuario usuario;

    @Column(length = 2048)
    private String accessToken;

    @Column(length = 512)
    private String refreshToken;

    @Column(name = "expires_at", columnDefinition = "DATETIME(6)")
    private Instant expiresAt;

    public void atualizarTokens(OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken) {
        this.setAccessToken(accessToken.getTokenValue());
        this.setExpiresAt(accessToken.getExpiresAt());
        if (refreshToken != null) {
            this.setRefreshToken(refreshToken.getTokenValue());
        }
    }
}
