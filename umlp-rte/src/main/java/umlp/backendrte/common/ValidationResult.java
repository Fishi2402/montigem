package umlp.backendrte.common;

import umlp.backendrte.command.StatusResult;

public class ValidationResult extends StatusResult {

  DomainValidationResult validationResult;

  public ValidationResult(long id, DomainValidationResult validationResult, String type) {
    super(id, !validationResult.hasErrors(), type);
    this.validationResult = validationResult;
  }

  @Override
  public boolean isValidationResult() {
    return true;
  }

  @Override
  public boolean isSuccessful() {
    return !validationResult.hasErrors();
  }

}
