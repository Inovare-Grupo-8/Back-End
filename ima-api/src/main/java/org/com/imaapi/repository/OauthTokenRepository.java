package org.com.imaapi.repository;

import org.com.imaapi.model.oauth.OauthToken;
import org.com.imaapi.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OauthTokenRepository extends JpaRepository<OauthToken, Integer>{
    Optional<OauthToken> findByIdUsuario(Integer id);
    boolean existsByIdUsuario(Long id);
    Void deleteByIdUsuario(Long id);
}
