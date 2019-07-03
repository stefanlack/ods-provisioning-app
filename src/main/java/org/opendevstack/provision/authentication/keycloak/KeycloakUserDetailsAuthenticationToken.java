package org.opendevstack.provision.authentication.keycloak;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;

import java.security.Principal;

public class KeycloakUserDetailsAuthenticationToken extends KeycloakAuthenticationToken {

  private UserDetails userDetails;

  public KeycloakUserDetailsAuthenticationToken(KeycloakAuthenticationToken token) {
    super(token.getAccount(), token.isInteractive(), token.getAuthorities());

    String username = this.resolveUsername(token);
    this.userDetails = new User(username, "N/A", token.getAuthorities());
  }

  @Override
  public Object getPrincipal() {
    return userDetails;
  }

  /**
   * Returns the username from the given {@link KeycloakAuthenticationToken}. By default, this
   * method resolves the username from the token's {@link KeycloakPrincipal}'s name. This value can
   * be controlled via <code>keycloak.json</code>'s <a
   * href="http://docs.jboss.org/keycloak/docs/1.2.0.CR1/userguide/html/ch08.html#adapter-config">
   * <code>principal-attribute</code></a>. For more fine-grained username resolution, override this
   * method.
   *
   * @param token the {@link KeycloakAuthenticationToken} from which to extract the username
   * @return the username to use when loading a user from the this provider's {@link
   *     UserDetailsService}.
   * @see UserDetailsService#loadUserByUsername
   * @see OidcKeycloakAccount#getPrincipal
   */
  protected static String resolveUsername(KeycloakAuthenticationToken token) {

    Assert.notNull(token, "KeycloakAuthenticationToken required");
    Assert.notNull(
        token.getAccount(), "KeycloakAuthenticationToken.getAccount() cannot be return null");
    OidcKeycloakAccount account = token.getAccount();
    Principal principal = account.getPrincipal();

    return principal.getName();
  }
}
