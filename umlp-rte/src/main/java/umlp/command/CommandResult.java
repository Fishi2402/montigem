/* (c) https://github.com/MontiCore/monticore */

package umlp.command;
// RTE-P

// todo: erläutern

import jsweet.lang.Replace;

/**
 * single result of an executed command
 */
public abstract class CommandResult<T> {
  protected long commandId;

  // Wird genutzt um den DeSer den Typ des Type Parameters T zum deserialisieren mitzuteilen
  protected final String typeParameter;

  protected CommandResult(long id, String typeParameter) {
    this.commandId = id;
    this.typeParameter = typeParameter;
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
  // Um zwischen StatusResult und SimpleResult<T> zu unterscheiden werden die folgenden Methoden genutzt,
  // welche verhindern, dass instanceof genutzt werden muss

  // Wird in StatusResult so überschrieben, dass true zurückgegeben wird
  public boolean isStatusResult(){
    return false;
  }

  @Replace("return this as unknown as StatusResult;")
  public StatusResult asStatusResult(){
    return (StatusResult) this;
  }

  public boolean isSimpleResult(){
    return false;
  }

  @Replace("return this as unknown as SimpleResult<T>;")
  public SimpleResult<T> asSimpleResult(){
    return (SimpleResult<T>) this;
  }

  public String getTypeParameter() {
    return typeParameter;
  }
}
