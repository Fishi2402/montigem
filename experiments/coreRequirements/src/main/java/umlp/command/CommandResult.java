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
}
