/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.opendevstack.provision.services;

import com.atlassian.crowd.integration.soap.SOAPGroup;
import org.opendevstack.provision.adapter.IProjectIdentityMgmtAdapter;
import org.opendevstack.provision.adapter.exception.IdMgmtException;
import org.opendevstack.provision.authentication.ProvisioningAppAuthenticationManager;
import org.opendevstack.provision.model.ProjectData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Identity mgmt adapter to create / validate groups
 *
 * @author utschig
 */
@Service
public class ProjectIdentityMgmtAdapter implements IProjectIdentityMgmtAdapter {
  private static final Logger logger = LoggerFactory.getLogger(ProjectIdentityMgmtAdapter.class);

  @Autowired ProvisioningAppAuthenticationManager manager;

  public void validateIdSettingsOfProject(ProjectData project) throws IdMgmtException {
    Map<String, String> projectCheckStatus = new HashMap<>();

    long startTime = System.currentTimeMillis();

    if (!groupExists(project.adminGroup)) {
      projectCheckStatus.put("adminGroup", project.adminGroup);
    }
    if (!groupExists(project.userGroup)) {
      projectCheckStatus.put("userGroup", project.userGroup);
    }
    if (!groupExists(project.readonlyGroup)) {
      projectCheckStatus.put("readonlyGroup", project.readonlyGroup);
    }
    if (!userExists(project.admin)) {
      projectCheckStatus.put("admin", project.admin);
    }

    logger.debug("identityCheck Name took (ms): {}", System.currentTimeMillis() - startTime);

    if (!projectCheckStatus.isEmpty()) {
      throw new IdMgmtException(
          "Identity check failed - these groups don't exist! " + projectCheckStatus);
    }
  }

  @Override
  @SuppressWarnings("squid:S1193")
  public boolean groupExists(String groupName) {
    if (groupName == null || groupName.trim().length() == 0) {
      return true;
    }
    long startTime = System.currentTimeMillis();
    try {
      boolean exists = manager.existsGroupWithName(groupName);
      if (!exists) {
        logger.error("group {0} does not exist!", groupName);
      }
      return exists;
    } finally {
      logger.debug(
          "existsGroupWithName by Name took (ms): {}", System.currentTimeMillis() - startTime);
    }
  }

  @Override
  @SuppressWarnings("squid:S1193")
  public boolean userExists(String userName) {
    if (userName == null || userName.trim().length() == 0) {
      return true;
    }

    long startTime = System.currentTimeMillis();
    try {
      boolean exists = manager.existPrincipalWithName(userName);
      if (!exists) {
        logger.error("principal {0} does not exist!", userName);
      }
      return exists;
    } finally {
      logger.debug("findPrincipal by Name took (ms): {}", System.currentTimeMillis() - startTime);
    }
  }

  @Override
  public String createUserGroup(String projectName) throws IdMgmtException {
    return createGroupInternal(projectName);
  }

  @Override
  public String createAdminGroup(String projectName) throws IdMgmtException {
    return createGroupInternal(projectName);
  }

  @Override
  public String createReadonlyGroup(String projectName) throws IdMgmtException {
    return createGroupInternal(projectName);
  }

  String createGroupInternal(String groupName) throws IdMgmtException {
    if (groupName == null || groupName.trim().length() == 0) {
      throw new IdMgmtException("Cannot create a null group!");
    }
    try {
      return manager.addGroup(groupName);
    } catch (IdMgmtException eAddGroup) {
      logger.error("Could not create group {}, error: {}", groupName, eAddGroup);
      throw eAddGroup;
    }
  }
}
