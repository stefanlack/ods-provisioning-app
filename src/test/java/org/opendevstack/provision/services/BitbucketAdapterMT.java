package org.opendevstack.provision.services;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendevstack.provision.SpringBoot;
import org.opendevstack.provision.adapter.IServiceAdapter.LIFECYCLE_STAGE;
import org.opendevstack.provision.model.OpenProjectData;
import org.opendevstack.provision.model.bitbucket.BitbucketProject;
import org.opendevstack.provision.storage.LocalStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = SpringBoot.class)
@ActiveProfiles("oauth2,oauth2private")
@Ignore("Only exists for manual execution")
public class BitbucketAdapterMT {

  @Autowired private BitbucketAdapter bitbucketAdapter;

  @Autowired private LocalStorage localStorage;

  @Test
  public void deletesProjects() {
    OpenProjectData project = localStorage.getProject("TEST6");
    bitbucketAdapter.cleanup(LIFECYCLE_STAGE.INITIAL_CREATION, project);
  }

  @Test
  public void loadBitbucketProject() {
    List<BitbucketProject> projects = bitbucketAdapter.getBitbucketProjects("");
    assertThat(projects, hasSize(greaterThan(0)));

    projects = bitbucketAdapter.getBitbucketProjects("OPENDEVSTACK");
    assertThat(projects, hasSize(1));

    projects = bitbucketAdapter.getBitbucketProjects("unkonwn");
    assertThat(projects, hasSize(0));
  }
}
