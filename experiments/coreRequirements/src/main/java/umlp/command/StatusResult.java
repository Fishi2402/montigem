package umlp.command;

import umlp.command.error.MontiGemError;

import java.util.Optional;

public class StatusResult extends CommandResult<Void> {
  protected Optional<MontiGemError> error;
  protected StatusResult(long id, MontiGemError error) {
    super(id);
    this.error = Optional.ofNullable(error);
  }

  public static StatusResult ok(long id){
    return new StatusResult(id, null);
  }

  public static StatusResult error(long id, MontiGemError error){
    return new StatusResult(id, error);
  }

  public boolean wasSuccessful(){
    return !error.isPresent();
  }

  public MontiGemError getError(){
    return error.get();
  }
}
