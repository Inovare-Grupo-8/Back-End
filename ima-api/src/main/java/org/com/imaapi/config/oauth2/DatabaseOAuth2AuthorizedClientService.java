package org.com.imaapi.config.oauth2;

import org.com.imaapi.model.oauth.OauthToken;
import org.com.imaapi.repository.OauthTokenRepository;
import org.com.imaapi.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("unchecked")
public class DatabaseOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {
    private final OauthTokenRepository oauthTokenRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final UsuarioRepository usuarioRepository;

    public DatabaseOAuth2AuthorizedClientService(OauthTokenRepository oauthTokenRepository,
                                                 ClientRegistrationRepository clientRegistrationRepository,
                                                 UsuarioRepository usuarioRepository) {
        this.oauthTokenRepository = oauthTokenRepository;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        return oauthTokenRepository.findByUsuarioEmail(principalName)
                .map(token -> {
                    ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);

                    return (T) new OAuth2AuthorizedClient(
                            registration,
                            principalName,
                            token.getAccessTokenObject(),
                            token.getRefreshTokenObject()
                    );
                }).orElse(null);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        String email = null;

        if (principal instanceof OAuth2AuthenticationToken token) {
            if (token.getPrincipal() instanceof OAuth2User user) {
                email = user.getAttribute("email");
            }
        }

        if (email == null) {
            email = principal.getName();
        }

        if (email != null) {
            usuarioRepository.findByEmail(email).ifPresent(usuario -> {
                OauthToken token = oauthTokenRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                        .orElseGet(() -> {
                            OauthToken novoToken = new OauthToken();
                            novoToken.setUsuario(usuario);
                            return novoToken;
                        });

                token.atualizarAccessToken(authorizedClient.getAccessToken());

                if (authorizedClient.getRefreshToken() != null) {
                    token.atualizarRefreshToken(authorizedClient.getRefreshToken());
                }

                oauthTokenRepository.save(token);
            });
        }
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        oauthTokenRepository.deleteByUsuarioEmail(principalName);
    }
}
