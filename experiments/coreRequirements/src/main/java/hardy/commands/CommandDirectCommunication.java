/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;

import stanley.FlensburgTestContext;
import umlp.command.Command;
import umlp.command.CommandCommunication;
import umlp.command.CommandManager;

public class CommandDirectCommunication implements CommandCommunication {
  protected FlensburgTestContext backendContext;
  // Wegen beidseitiger navigation muss dieser per setter gesetzt werden
  protected FlensburgTestContext clientContext;

  public CommandDirectCommunication(FlensburgTestContext backendContext) {
    this.backendContext = backendContext;
  }

  public FlensburgTestContext getClientContext() {
    return clientContext;
  }

  public void setClientContext(FlensburgTestContext clientContext) {
    this.clientContext = clientContext;
  }

  @Override
  public void sendCommand(Command command) {
    clientContext.activate();
    CommandManager.executeCommand(command);
    backendContext.activate();
  }
}
