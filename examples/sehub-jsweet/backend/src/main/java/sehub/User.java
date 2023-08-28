/* (c) https://github.com/MontiCore/monticore */
package sehub;

import input.src.lib.gemtextinput.GemTextInputValidity;

public class User extends UserTOP {

  public GemTextInputValidity getValidityEmail() {
    return new GemTextInputValidity();
  }

  public GemTextInputValidity getValidityUsername() {
    return new GemTextInputValidity();
  }

  public GemTextInputValidity getValidityPassword() {
    return new GemTextInputValidity();
  }
}
