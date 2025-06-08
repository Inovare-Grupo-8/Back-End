package org.com.imaapi.model.oauth;

import jakarta.persistence.*;
import lombok.Data;
import org.com.imaapi.model.usuario.Usuario;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "oauth_token")
@Data
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
    private String accessToken;

    @Column(length = 512)
    private String refreshToken;

    @Column(name = "expira_em")
    private Instant expiresAt;

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

    public void atualizarTokens(String accessToken, String refreshToken, Instant expiresAt) {
        this.setAccessToken(accessToken);
        this.setExpiresAt(expiresAt);
        if (refreshToken != null) {
            this.setRefreshToken(refreshToken);
        }
    }
}
