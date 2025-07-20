package sehub;

import mc.fenix.input.gemtextinputtypes.GemTextInputValidity;

public class User extends UserTOP {

  protected String statusUsername = "";
  protected String validityUsername = "";

  protected String statusPassword = "";
  protected String validityPassword = "";

  protected String statusEmail = "";
  protected String validityEmail = "";

  protected String statusRole = "";
  protected String validityRole = "";

  public GemTextInputValidity getValidityUsername() {
    return new GemTextInputValidity(this.statusUsername, this.validityUsername);
  }

  public void setValidityUsername(String value) {
    this.validityUsername = value;
  }

  public void setStatusUsername(String value) {
    this.statusUsername = value;
  }

  public GemTextInputValidity getValidityPassword() {
    return new GemTextInputValidity(this.statusPassword, this.validityPassword);
  }

  public void setValidityPassword(String value) {
    this.validityPassword = value;
  }

  public void setStatusPassword(String value) {
    this.statusPassword = value;
  }

  public GemTextInputValidity getValidityEmail() {
    return new GemTextInputValidity(this.statusEmail, this.validityEmail);
  }

  public void setValidityEmail(String value) {
    this.validityEmail = value;
  }

  public void setStatusEmail(String value) {
    this.statusEmail = value;
  }

  public GemTextInputValidity getValidityRole() {
    return new GemTextInputValidity(this.statusRole, this.validityRole);
  }

  public void setValidityRole(String value) {
    this.validityRole = value;
  }

  public void setStatusRole(String value) {
    this.statusRole = value;
  }

  @Override
  public SehubRole getRole() {
    SehubRole res = super.getRole();
    if (res == null) {
      this.statusRole = "Error";
      // this.validityRole = "The role must be set!";
    } else {
      this.validityRole = "";
      this.statusRole = "";
    }
    return res;
  }

  public void resetValidity() {
    this.statusUsername = "";
    this.statusEmail = "";
    this.statusPassword = "";
    this.validityUsername = "";
    this.validityEmail = "";
    this.validityPassword = "";
    this.validityRole = "";
  }
}
