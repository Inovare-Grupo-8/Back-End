package org.com.imaapi.model.oauth;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Oauth2AuthorizedClientId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "client_registration_id", length = 100, nullable = false)
    private String clientRegistrationId;

    @Column(name = "principal_name", length = 200, nullable = false)
    private String principalName;
}
