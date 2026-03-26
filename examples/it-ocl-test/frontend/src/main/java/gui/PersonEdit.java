package gui;

import mc.fenix.input.geminputbasetypes.ValidationError;
import mc.fenix.input.gemtextinputtypes.GemTextInputValidity;
import mydomain.errorMessages.ErrorMsgs1;
import umlp.jsweet.extension.annotation.Component;

import java.util.Optional;

@Component
public class PersonEdit extends PersonEditTOP {
  @Override
  public GemTextInputValidity getGemTextInput1Validity() {
    ValidationError errorOpt = ErrorMsgs1.getErrorInv0(getP());
    if(errorOpt != null){
      return new GemTextInputValidity("Error", errorOpt.getShortError());
    }

    return null;
  }
}
