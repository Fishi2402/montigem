/* (c) https://github.com/MontiCore/monticore */
package dtcockpit;

import input.GemTextInputValidity;

public class User extends UserTOP {

  public GemTextInputValidity getValidityEmail() {
    return null;
  }

  public GemTextInputValidity getValidityUsername() {
    return null;
  }

  public GemTextInputValidity getValidityPassword() {
    return null;
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

}
