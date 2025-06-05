package org.com.imaapi.repository;

import org.com.imaapi.model.oauth.OauthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OauthTokenRepository extends JpaRepository<OauthToken, Integer> {
    Optional<OauthToken> findByUsuarioIdUsuario(Integer id);

    boolean existsByUsuarioIdUsuario(Integer id);

    void deleteByUsuarioIdUsuario(Integer id);
}
