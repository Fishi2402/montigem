/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.command;

// RTE-P

// Repräsentiert das Ergebnis eines ausgeführten Commands ohne zusätzliche Rückgabe, z.B. eines set Commands für ein
// Attribut. Der Sender des Commands muss trotzdem wissen, ob dieser erfolgreich ausgeführt wurde.
public class StatusResult extends CommandResult<Void> {
  protected boolean successful;
  protected StatusResult(long id, boolean successful) {
    super(id, "java.lang.Void");
    this.successful = successful;
  }

  protected StatusResult(long id, boolean successful, String type) {
    super(id, type);
    this.successful = successful;
  }

  public static StatusResult ok(long id){
    return new StatusResult(id, true);
  }

  public static StatusResult error(long id){
    return new StatusResult(id, false);
  }

  public boolean wasSuccessful(){
    return successful;
  }

  @Override
  public boolean isSuccessful() {
    return successful;
  }

  @Override
  public boolean isStatusResult() {
    return true;
  }

  public boolean isValidationResult() {
    return false;
  }
}
