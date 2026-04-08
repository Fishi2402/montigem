package gui;

import mc.fenix.input.geminputbasetypes.ValidationError;
import mc.fenix.input.gemtextinputtypes.GemTextInputValidity;
import umlp.jsweet.extension.annotation.Component;

import java.util.Optional;

@Component
public class PersonEdit extends PersonEditTOP {
  @Override
  public GemTextInputValidity getGemTextInput1Validity() {
    Optional<ValidationError> errorOpt = getP().getAgeValidationError();
    if(errorOpt.isPresent()){
      ValidationError error = errorOpt.get();
      return new GemTextInputValidity("Error", error.getShortError());
    }

    return null;
  }
}
