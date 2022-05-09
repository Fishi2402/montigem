/* (c) https://github.com/MontiCore/monticore */

// todo: Zweck unklar; Streichen? !!!


package umlp.command.send;
// RTE-P
/**
 * Promise to wait for the result of a command execution
 */
public class CommandPromise {
  private long id;
  private CommandCallback<Object> method;

  public CommandPromise(long id, CommandCallback<Object> method) {
    this.id = id;
    this.method = method;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public CommandCallback<Object> getMethod() {
    return method;
  }

  public void setMethod(CommandCallback<Object> method) {
    this.method = method;
  }
}
