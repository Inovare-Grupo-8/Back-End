package org.com.imaapi.repository;

import org.com.imaapi.model.oauth.Oauth2AuthorizedClientEntity;
import org.com.imaapi.model.oauth.Oauth2AuthorizedClientId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Oauth2AuthorizedClientRepository extends JpaRepository<Oauth2AuthorizedClientEntity, Oauth2AuthorizedClientId> {
    Optional<Oauth2AuthorizedClientEntity> findById_PrincipalName(String principalName);
    Optional<Oauth2AuthorizedClientEntity> findById_ClientRegistrationIdAndId_PrincipalName(String clientRegistrationId, String principalName);
    List<Oauth2AuthorizedClientEntity> findById_ClientRegistrationId(String clientRegistrationId);
    void deleteById_PrincipalName(String principalName);
}
