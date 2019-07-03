package org.opendevstack.provision.authentication.keycloak;

import org.opendevstack.provision.adapter.exception.IdMgmtException;
import org.opendevstack.provision.authentication.ProvisioningAppAuthenticationManager;
import org.opendevstack.provision.authentication.SessionAwarePasswordHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component("provisioningAppAuthenticationManager")
@ConditionalOnProperty(name = "provision.auth.provider", havingValue = "keycloak")
public class KeycloakAuthenticationManager implements ProvisioningAppAuthenticationManager {

    @Autowired
    private SessionAwarePasswordHolder userPassword;

    @Override
    public String getUserPassword() {
        return userPassword.getPassword();
    }

    @Override
    public void setUserPassword(String userPassword) {
        this.userPassword.setPassword(userPassword);
    }

    @Override
    public boolean existsGroupWithName(String groupName) {
        return false;
    }

    @Override
    public boolean existPrincipalWithName(String userName) {
        return false;
    }

    @Override
    public String addGroup(String groupName) throws IdMgmtException {
        return null;
    }

}
