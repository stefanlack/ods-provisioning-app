package org.opendevstack.provision.util.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamingRule {
  String reg;
  Boolean not;
  String description;
  String errorMessage;

  public String getReg() {
    return reg;
  }

  public void setReg(String reg) {
    this.reg = reg;
  }

  public Boolean getNot() {
    return not;
  }

  public void setNot(Boolean not) {
    this.not = not;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * determines whether the candidateName is a valid name according this rule
   *
   * @param candidateName the name to check
   * @return the result
   */
  public boolean isValidName(String candidateName) {
    Matcher m = Pattern.compile(this.reg).matcher(candidateName);
    return this.not ? !m.find() : m.find();
  }
}
