package umlp.backendrte.common;

import umlp.backendrte.command.CommandResult;

import java.util.List;

public class ValidationResult extends CommandResult<Void> {

  List<ValidationMessage> messages;

  public ValidationResult(long id, List<ValidationMessage> messages) {
    super(id, "Void");
    this.messages = messages;
  }

  @Override
  public boolean isSuccessful() {
    return this.messages.stream().noneMatch(m -> m.getStatus() == ValidationStatus.ERROR);
  }

  public boolean isValidationResult() {
    return true;
  }

  public List<ValidationMessage> get(){
    return messages;
  }

}
