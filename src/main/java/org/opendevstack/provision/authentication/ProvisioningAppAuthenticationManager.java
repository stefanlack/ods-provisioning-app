package org.opendevstack.provision.authentication;

import org.opendevstack.provision.adapter.exception.IdMgmtException;

public interface ProvisioningAppAuthenticationManager {
    String getUserPassword();

    void setUserPassword(String userPassword);

    boolean existsGroupWithName(String groupName);

    boolean existPrincipalWithName(String userName);

    String addGroup(String groupName) throws IdMgmtException;

}
