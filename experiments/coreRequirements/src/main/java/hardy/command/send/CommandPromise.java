/* (c) https://github.com/MontiCore/monticore */

package hardy.command.send;

import hardy.dto.DTO;

/**
 * Promise to wait for the result of a command execution
 */
public class CommandPromise {
  private long id;
  private CommandCallback<DTO> method;

  public CommandPromise(long id, CommandCallback<DTO> method) {
    this.id = id;
    this.method = method;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public CommandCallback<DTO> getMethod() {
    return method;
  }

  public void setMethod(CommandCallback<DTO> method) {
    this.method = method;
  }
}
