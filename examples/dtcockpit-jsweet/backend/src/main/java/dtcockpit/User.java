/* (c) https://github.com/MontiCore/monticore */
package dtcockpit;

import mc.fenix.input.GemTextInputValidity;

public class User extends UserTOP {

  private String _statusUsername = "";

  private String _validityUsername = "";

  public int getHash (String value) {
    int hash = 0, chr = 0;

    if (value.length() == 0) {
      return hash;
    }
    for (int i = 0; i < value.length(); i++) {
      chr = value.charAt(i);
      hash = ((hash << 5) - hash) + chr;
      hash |= 0; // Convert to 32bit integer
    }
    return hash;
  }

  public GemTextInputValidity getValidityUsername() {
    return new GemTextInputValidity(this._statusUsername, this._validityUsername);
  }

  public void setValidityUsername(String value) {
    this._validityUsername = value;
  }

  public void setStatusUsername(String value) {
    this._statusUsername = value;
  }

  public GemTextInputValidity validityEmail() {
    return getValidityEmail();
  }

  public GemTextInputValidity validityUsername() {
    return getValidityUsername();
  }

  public GemTextInputValidity validityPassword() {
    return getValidityPassword();
  }



  private String _statusPassword = "";
  private String _validityPassword = "";

  public GemTextInputValidity getValidityPassword() {
    return new GemTextInputValidity(this._statusPassword, this._validityPassword);
  }

  public void setValidityPassword(String value) {
    this._validityPassword = value;
  }

  public void setStatusPassword(String value) {
    this._statusPassword = value;
  }

  private String _statusEmail = "";
  private String _validityEmail = "";

  public GemTextInputValidity getValidityEmail() {
    return new GemTextInputValidity(this._statusEmail, this._validityEmail);
  }

  public int getAvatar() {
    var avatars = 6;
    return ((this.getHash(this.getUsername()) % avatars) + avatars) % avatars;
  }

  public void setValidityEmail(String value) {
    this._validityEmail = value;
  }

  public void setStatusEmail(String value) {
    this._statusEmail = value;
  }

  private String _statusRole = "";

  private String _validityRole = "";

  public GemTextInputValidity getValidityRole() {
    return new GemTextInputValidity(this._statusRole, this._validityRole);
  }

  public void setValidityRole(String value) {
    this._validityRole = value;
  }

  public void setStatusRole(String value) {
    this._statusRole = value;
  }



  @Override
  public DtcockpitRole getRole() {
    DtcockpitRole res = super.getRole();
    if (res == null) {
      // this.validityRole = 'The role must be set!';
      this._statusRole = "Error";
    } else {
      this._validityRole = "";
      this._statusRole = "";
    }
    return res;
  }

  public void resetValidity() {
    this._statusUsername = "";
    this._statusEmail = "";
    this._statusPassword = "";
    this._validityUsername = "";
    this._validityEmail = "";
    this._validityPassword = "";
    this._validityRole = "";
    this._validityRole = "";
  }



}
