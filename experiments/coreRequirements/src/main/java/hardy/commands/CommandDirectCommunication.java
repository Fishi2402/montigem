/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;

import stanley.FlensburgTestContext;
import umlp.command.*;

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
  public <T> void sendCommand(Command<T> command) {
    clientContext.activate();
    CommandManager.executeCommand(command);
    backendContext.activate();
  }

  @Override
  public <T> CommandResult<T> sendCommandAndReceiveResult(Command<T> command) {
    backendContext.activate();
    // Use (de)-serialization both ways to simulate Communication
    // Also effectively clones objects, allowing us to test the sync mechanisms

    String cmdStr = CommandObjectMapper.serializeCommand(command);
    Command<?> recreatedCommand = CommandObjectMapper.deserializeCommand(cmdStr);

    CommandResult<?> res = CommandManager.executeCommand(recreatedCommand);
    String resStr = CommandObjectMapper.serializeResponse(res);

    clientContext.activate();
    CommandResult<?> recreatedRes = CommandObjectMapper.deserializeResponse(resStr);
    return (CommandResult<T>) recreatedRes;
  }

  @Override
  public long init() {
    return backendContext.getCommandManager()._register(new Participant(
        new CommandDirectCommunication(backendContext)));
  }
}
