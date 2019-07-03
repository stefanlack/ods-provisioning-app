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

package org.opendevstack.provision.controller;

import org.opendevstack.provision.authentication.ProvisioningAppAuthenticationManager;
import org.opendevstack.provision.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default controller for navigating the page. Autowiring per setter because of testability
 *
 * @author Brokmeier, Pascal
 */
@Controller
public class DefaultController {

  StorageAdapter storageAdapter;

  ProvisioningAppAuthenticationManager manager;

  private RundeckAdapter rundeckAdapter;

  private JiraAdapter jiraAdapter;

  private BitbucketAdapter bitbucketAdapter;

  @Autowired private ConfluenceAdapter confluenceAdapter;

  private static final String LOGIN_REDIRECT = "redirect:/login";

  private static final String ACTIVE = "active";

  @Value("${crowd.user.group}")
  private String crowdUserGroup;

  @Value("${crowd.admin.group}")
  private String crowdAdminGroup;

  @Value("${openshift.project.upgrade}")
  private boolean ocUpgradeAllowed;

  @Autowired List<String> projectTemplateKeyNames;

  @Value("${crowd.sso.cookie.name}")
  private String crowdCookieKey;

  @Value("${provision.auth.provider}")
  private String authProvider;

  @RequestMapping("/")
  public String rootRedirect(Model model) {
      return LOGIN_REDIRECT;
  }

  @RequestMapping("/home")
  public String home(Model model) {
    if (!isAuthenticated()) {
      return LOGIN_REDIRECT;
    }
    model.addAttribute("classActiveHome", ACTIVE);
    return "home";
  }

  @RequestMapping("/provision")
  public String provisionProject(
      Model model,
      Authentication authentication,
      @CookieValue(value = "crowd.token_key", required = false) String crowdCookie,
      HttpServletRequest request) {
    if (!isAuthenticated()) {
      return LOGIN_REDIRECT;
    } else {

      model.addAttribute("jiraProjects", storageAdapter.listProjectHistory());

      model.addAttribute("quickStarter", rundeckAdapter.getQuickstarter());
      model.addAttribute("crowdUserGroup", crowdUserGroup.toLowerCase());
      model.addAttribute("crowdAdminGroup", crowdAdminGroup.toLowerCase());
      model.addAttribute("ocUpgradeAllowed", ocUpgradeAllowed);

      model.addAttribute("projectTypes", projectTemplateKeyNames);
    }
    model.addAttribute("classActiveNew", ACTIVE);
    return "provision";
  }

  @RequestMapping("/login")
  public String login(Model model) {
    if (isAuthProviderCrowd()) {
      return "crowdLogin";
    }

    return "keycloakLogin";
  }

  @RequestMapping("/history")
  public String history(Model model) {
    if (!isAuthenticated()) {
      return LOGIN_REDIRECT;
    }
    model.addAttribute("classActiveHistory", ACTIVE);
    model.addAttribute("projectHistory", storageAdapter.listProjectHistory());
    return "history";
  }

  @RequestMapping("/about")
  public String about(Model model) {
    if (!isAuthenticated()) {
      return LOGIN_REDIRECT;
    }
    model.addAttribute("classActiveAbout", ACTIVE);
    model.addAttribute("aboutChanges", storageAdapter.listAboutChangesData().aboutDataList);

    // add endpoint map
    Map<String, String> endpoints = new HashMap<>();
    endpoints.put("JIRA", jiraAdapter.getEndpointUri());
    endpoints.put("GIT", bitbucketAdapter.getEndpointUri());
    endpoints.put("RUNDECK", rundeckAdapter.getRundeckAPIPath());
    endpoints.put("CONFLUENCE", confluenceAdapter.getConfluenceAPIPath());

    model.addAttribute("endpointMap", endpoints);

    model.addAttribute("crowdUserGroup", crowdUserGroup.toLowerCase());
    model.addAttribute("crowdAdminGroup", crowdAdminGroup.toLowerCase());
    return "about";
  }

  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      manager.setUserPassword(null);
      new SecurityContextLogoutHandler().logout(request, response, auth);
    }
    return "redirect:/login?logout";
  }

  private boolean isAuthenticated() {
    if (isAuthProviderCrowd()) {
      return (manager.getUserPassword() != null);
    }
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean authenticated = authentication.isAuthenticated();

    return authenticated;
  }

  private boolean isAuthProviderCrowd() {
    return authProvider.equals("crowd");
  }

  @Autowired
  public void setCustomAuthenticationManager(ProvisioningAppAuthenticationManager manager) {
    this.manager = manager;
  }

  @Autowired
  public void setRundeckAdapter(RundeckAdapter rundeckAdapter) {
    this.rundeckAdapter = rundeckAdapter;
  }

  @Autowired
  public void setStorageAdapter(StorageAdapter storageAdapter) {
    this.storageAdapter = storageAdapter;
  }

  @Autowired
  public void setJiraAdapter(JiraAdapter jiraAdapter) {
    this.jiraAdapter = jiraAdapter;
  }

  @Autowired
  public void setBitbucketAdapter(BitbucketAdapter bitbucketAdapter) {
    this.bitbucketAdapter = bitbucketAdapter;
  }
}
