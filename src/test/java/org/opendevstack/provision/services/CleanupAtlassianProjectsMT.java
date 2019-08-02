package org.opendevstack.provision.services;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendevstack.provision.SpringBoot;
import org.opendevstack.provision.model.bitbucket.BitbucketProject;
import org.opendevstack.provision.util.CredentialsInfo;
import org.opendevstack.provision.util.rest.RestClient;
import org.opendevstack.provision.util.rest.RestClientCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = SpringBoot.class)
@ActiveProfiles("oauth2,oauth2private")
// @Ignore("Only exists for manual execution")
public class CleanupAtlassianProjectsMT {

  @Autowired private RestClient restClient;

  @Autowired private JiraAdapter jiraAdapter;
  @Autowired private ConfluenceAdapter confluenceAdapter;
  @Autowired private BitbucketAdapter bitbucketAdapter;

  @Autowired private Environment environment;

  private CredentialsInfo jiraCredentials;
  private CredentialsInfo confluenceCredentials;
  private CredentialsInfo bitbucketCredentials;

  @Before
  public void setUp() {
    jiraCredentials = buildCredentials("jira");
    confluenceCredentials = buildCredentials("confluence");
    bitbucketCredentials = buildCredentials("bitbucket");
  }

  private CredentialsInfo buildCredentials(String configurationPrefix) {
    String propertyAdminUserKey = configurationPrefix + ".admin_user";
    String propertyAdminUserPasswordKey = configurationPrefix + ".admin_password";
    return new CredentialsInfo(
        environment.getProperty(propertyAdminUserKey),
        environment.getProperty(propertyAdminUserPasswordKey));
  }

  @Test
  public void cleanupJira() {
    jiraAdapter.getProjects("").keySet().forEach(this::deleteJiraProject);
  }

  @Test
  public void cleanupConfluence() throws IOException {
    Set<String> keys = confluenceAdapter.getProjects("").keySet();
    Set<String> filteredKeys =
        keys.stream().filter(key -> !asList("ds", "OP").contains(key)).collect(Collectors.toSet());
    assertThat("2 spaces should not be deleted", keys.size() - filteredKeys.size(), equalTo(2));
    filteredKeys.forEach(this::deleteConfluenceProject);
  }

  @Test
  public void cleanupBitbucket() {
    Map<String, String> projects = bitbucketAdapter.getProjects("");

    Set<String> keys = projects.keySet();
    Set<String> filteredKeys =
        keys.stream()
            .filter(key -> !asList("OPENDEVSTACK").contains(key))
            .collect(Collectors.toSet());
    assertThat("1 project should not be deleted", keys.size() - filteredKeys.size(), equalTo(1));

    filteredKeys.forEach(this::deleteBitbucketProject);
  }

  private void deleteBitbucketProject(String projectKey) {
    List<String> reproSlugs = readBitbucketRepoSlugs(projectKey);
    reproSlugs.forEach(slug ->deleteBitbucketRepo(projectKey,slug));
    executeDeleteOperation(
        this.bitbucketCredentials,
        "http://192.168.56.31:7990/rest/api/1.0/projects/%s",
        projectKey);
  }


  private List<String> readBitbucketRepoSlugs(String projectKey) {
    List<String> reproSlugs;
    RestClientCall call =
        RestClientCall.get()
            .url("http://192.168.56.31:7990/rest/api/1.0/projects/%s/repos?limit=1000", projectKey)
            .returnTypeReference(new TypeReference<List<JsonNode>>() {})
            .basicAuthenticated(bitbucketCredentials);

    try {
       reproSlugs= restClient.<List<JsonNode>>execute(call).get(0).path("values").findValuesAsText("slug");
    } catch (IOException e) {
      fail(e.getMessage());
    }
    reproSlugs = Collections.emptyList();
    return reproSlugs;
  }

  private void deleteBitbucketRepo(String projektKey,String reproSlug) {
    RestClientCall call = RestClientCall.delete()
        .url("http://192.168.56.31:7990/rest/api/1.0/projects/%s/repos/%s", projektKey, reproSlug)
        .basicAuthenticated(bitbucketCredentials);
    try {
      restClient.execute(call);
    } catch (IOException e) {
      fail("Cannot delete repo "+reproSlug +" " +e.getMessage());
    }
  }

  private void deleteConfluenceProject(String projectKey) {
    executeDeleteOperation(
        this.jiraCredentials, "http://192.168.56.31:8090/rest/api/latest/space/%s", projectKey);
  }

  private void deleteJiraProject(String projectKey) {
    executeDeleteOperation(
        this.jiraCredentials, "http://192.168.56.31:8080/rest/api/latest/project/%s", projectKey);
  }

  private void executeDeleteOperation(
      CredentialsInfo credentials, String urlFormat, String projectKey) {
    RestClientCall call =
        RestClientCall.delete().basicAuthenticated(credentials).url(urlFormat, projectKey);
    try {
      restClient.execute(call);
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
