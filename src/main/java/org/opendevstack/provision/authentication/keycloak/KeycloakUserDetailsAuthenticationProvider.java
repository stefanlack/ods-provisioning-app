package org.opendevstack.provision.authentication.keycloak;

import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class KeycloakUserDetailsAuthenticationProvider extends KeycloakAuthenticationProvider {

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    KeycloakAuthenticationToken token =
        (KeycloakAuthenticationToken) super.authenticate(authentication);

    if (token == null) {
      return null;
    }

    return new KeycloakUserDetailsAuthenticationToken(token);
  }
}
