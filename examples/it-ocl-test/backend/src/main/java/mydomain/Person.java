package mydomain;

import mc.fenix.input.geminputbasetypes.ValidationError;
import mydomain.errorMessages.ErrorMsgs1;

import java.util.Optional;

public class Person extends PersonTOP {
  public Optional<ValidationError> getAgeValidationError() {
    return ErrorMsgs1.getErrorInv0(this);
  }

  public boolean checkOCLConstraints() {
    return ErrorMsgs1.checkInv0(this);
  }
}
