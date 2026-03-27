package gui;

import mc.fenix.input.geminputbasetypes.ValidationError;
import mc.fenix.input.gemtextinputtypes.GemTextInputValidity;
import umlp.jsweet.extension.annotation.Component;

import java.util.Optional;

@Component
public class PersonEdit extends PersonEditTOP {
  @Override
  public GemTextInputValidity getGemTextInput1Validity() {
    ValidationError errorOpt = getP().getAgeValidationError();
    if(errorOpt != null){
      return new GemTextInputValidity("Error", errorOpt.getShortError());
    }

    return null;
  }
}
