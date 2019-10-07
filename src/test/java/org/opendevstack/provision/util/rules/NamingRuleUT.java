package org.opendevstack.provision.util.rules;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class NamingRuleUT {

  @Test
  public void testRuleWithNotEqualToTrue() {

    NamingRule rule = new NamingRule();
    rule.setReg("-\\d");
    rule.setNot(Boolean.TRUE);
    rule.setDescription("No digits should follow '-' sign");
    rule.setErrorMessage("Angular does not support '-' directly followed by the number");
    checkValidName(rule, "xy-valid");
    checkValidName(rule, "xyvalid");
    checkInvalidName(rule, "xy-1invalid");
  }

  @Test
  public void testRuleWithNotEqualToFalse() {
    NamingRule rule = new NamingRule();
    rule.setReg("-\\d");
    rule.setNot(Boolean.FALSE);
    rule.setDescription("Digits should follow '-' sign");
    rule.setErrorMessage("The character  '-' has directly be followed by at least one number");
    checkValidName(rule, "xy-1valid");

    checkInvalidName(rule, "xy-invalid");
    checkInvalidName(rule, "no minus sign at all");
  }

  private void checkValidName(NamingRule rule, String candidateName) {
    assertThat(rule.isValidName(candidateName))
        .as("Name '%s' is valid according to rule", candidateName)
        .isTrue();
  }

  private void checkInvalidName(NamingRule rule, String candidateName) {
    assertThat(rule.isValidName(candidateName))
        .as("Name '%s' is NOT valid according to rule", candidateName)
        .isFalse();
  }
}
