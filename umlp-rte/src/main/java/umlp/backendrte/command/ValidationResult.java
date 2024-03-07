package umlp.backendrte.command;

import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.DomainValidationResult;

public class ValidationResult extends StatusResult {

  DomainValidationResult validationResult;

  public ValidationResult(long id, DomainValidationResult validationResult, String type) {
    super(id, !validationResult.hasErrors(), type);
    this.validationResult = validationResult;
  }

  @Override
  public boolean isStatusResult() {
    return false;
  }

  @Override
  public boolean isValidationResult() {
    return true;
  }

  @Override
  public boolean isSuccessful() {
    return !validationResult.hasErrors();
  }

  public DomainValidationResult get() {
    return validationResult;
  }

}
