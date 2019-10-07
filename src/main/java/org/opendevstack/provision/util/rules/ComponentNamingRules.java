package org.opendevstack.provision.util.rules;

import java.util.List;

public class ComponentNamingRules {
  private String name;
  private List<NamingRule> rules;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<NamingRule> getRules() {
    return rules;
  }

  public void setRules(List<NamingRule> rules) {
    this.rules = rules;
  }

  public boolean isValidComponentId(String componentIdCandidate) {
    return !this.rules.stream()
        .map(namingRule -> namingRule.isValidName(componentIdCandidate))
        .anyMatch((res) -> res == false);
  }
}
