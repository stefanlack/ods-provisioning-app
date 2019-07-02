package org.opendevstack.provision.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendevstack.provision.SpringBoot;
import org.opendevstack.provision.model.rundeck.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringBoot.class)
@DirtiesContext
public class RundeckAdapterMT {

    @Autowired
    private RundeckAdapter rundeckAdapter;

  @Test
  public void getQuickstarter() {
      List<Job> quickstarter = rundeckAdapter.getQuickstarter();
      assertThat(quickstarter).isNotNull();
  }


}
