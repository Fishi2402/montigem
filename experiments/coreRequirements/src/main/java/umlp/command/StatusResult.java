package umlp.command;

// RTE-P

import umlp.command.error.MontiGemError;

import java.util.Optional;

// Repräsentiert das Ergebnis eines ausgeführten Commands ohne zusätzliche Rückgabe, z.B. eines set Commands für ein
// Attribut. Der Sender des Commands muss trotzdem wissen, ob dieser erfolgreich ausgeführt wurde.
public class StatusResult extends CommandResult<Void> {
  protected Optional<MontiGemError> error;
  protected StatusResult(long id, MontiGemError error) {
    super(id, "java.lang.Void");
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

  @Override
  public boolean isStatusResult() {
    return true;
  }
}
