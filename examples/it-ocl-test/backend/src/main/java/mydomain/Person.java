package mydomain;

import mc.fenix.input.geminputbasetypes.ValidationError;
import mydomain.errorMessages.ErrorMsgs1;

public class Person extends PersonTOP {
  public ValidationError getAgeValidationError() {
    return ErrorMsgs1.getErrorInv0(this);
  }

  public boolean checkOCLConstraints() {
    return ErrorMsgs1.checkInv0(this);
  }
}
