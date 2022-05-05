/* (c) https://github.com/MontiCore/monticore */

package hardy.command.send;

import hardy.command.DomainObject;

/**
 * Promise to wait for the result of a command execution
 */
public class CommandPromise {
  private long id;
  private CommandCallback<DomainObject> method;

  public CommandPromise(long id, CommandCallback<DomainObject> method) {
    this.id = id;
    this.method = method;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public CommandCallback<DomainObject> getMethod() {
    return method;
  }

  public void setMethod(CommandCallback<DomainObject> method) {
    this.method = method;
  }
}
