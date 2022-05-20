/* (c) https://github.com/MontiCore/monticore */

package umlp.command;
// RTE-P

// todo: erläutern

/**
 * single result of an executed command
 */
public abstract class CommandResult<T> {
  protected long commandId;

  protected CommandResult(long id) {
    this.commandId = id;
  }

  public long getCommandId() {
    return commandId;
  }

  public void setCommandId(long commandId) {
    this.commandId = commandId;
  }


  // Durch Javas Type erasure ist es nicht möglich geschachtelte generics in einer Vererbung zu benutzten,
  // also kann der genaue return type von Command#doRun nicht per Typenparameter gesetzt werden.
  // Stattdessen wird immer CommandResult<T> zurückgegeben.
  // Um zwischen StatusResult und GemResult<T> zu unterscheiden werden die folgenden Methoden genutzt,
  // welche verhindern, dass instanceof genutzt werden muss

  // Wird in GemResult so überschrieben, dass true zurückgegeben wird
  public boolean isGemResult(){
    return false;
  }

  public GemResult<T> asGemResult(){
    return (GemResult<T>) this;
  }

  // Wird in StatusResult so überschrieben, dass true zurückgegeben wird
  public boolean isStatusResult(){
    return false;
  }

  public StatusResult asStatusResult(){
    return (StatusResult) this;
  }
}
