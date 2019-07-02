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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.opendevstack.provision.model.AboutChangesData;
import org.opendevstack.provision.model.ProjectData;
import org.opendevstack.provision.storage.IStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.atlassian.crowd.integration.springsecurity.user.CrowdUserDetails;

/**
 * Service to interact with the underlying storage system to liast the project history
 *
 * @author Torsten Jaeschke
 */

@Service
public class StorageAdapter {

  @Autowired
  IStorage storage;

  @Value("${provision.auth.provider}")
  private String authProvider;

  private static final Logger logger = LoggerFactory.getLogger(StorageAdapter.class);

  public Map<String, ProjectData> listProjectHistory() {
	  Map<String, ProjectData> allProjects = storage.listProjectHistory();
	  //TODO stefanlack: implemented project filtering for keycloak, since this was not implemented in opitz repository/branch https://git.opitz-consulting.de/projects/OPENDEVSTACK/repos/ods-provisioning-app/browse?at=refs%2Fheads%2Fkeycloak-auth-tun.
	  if (authProvider.equals("keycloak")) {
          return allProjects;
      }
      return filterProjectsByPrincipal(allProjects);

  }

	private Map<String, ProjectData> filterProjectsByPrincipal(Map<String, ProjectData> allProjects) {
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			return new HashMap<>();
		}

		CrowdUserDetails userDetails =
		   (CrowdUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		// security!
		if (userDetails == null) {
			return new HashMap<>();
		}


		Map<String, ProjectData> filteredProjects = new HashMap<>();

		Collection<GrantedAuthority> authorities = userDetails.getAuthorities();
		logger.debug("User: {} \n {}", userDetails.getUsername(), authorities);

		for (Map.Entry<String, ProjectData> project : allProjects.entrySet())
		{
			ProjectData projectData = project.getValue();
			logger.debug("Project: {} groups: {},{} > {}",
					projectData.key, projectData.adminGroup,
					projectData.userGroup,  projectData.createpermissionset);

			if (!projectData.createpermissionset)
			{
				filteredProjects.put(projectData.key, projectData);
			} else
			{
				for (GrantedAuthority authority : authorities)
				{
					if (authority.getAuthority().equalsIgnoreCase(projectData.adminGroup) ||
						authority.getAuthority().equalsIgnoreCase(projectData.userGroup))
					{
						filteredProjects.put(projectData.key, projectData);
						break;
					}
				}
			}
		}

		return filteredProjects;
	}

	public ProjectData getProject(String key) {
	return storage.getProject(key);
  }

  public AboutChangesData listAboutChangesData() {
    return storage.listAboutChangesData();
  }

  void setStorage (IStorage storage) {
	this.storage = storage;
  }
}
