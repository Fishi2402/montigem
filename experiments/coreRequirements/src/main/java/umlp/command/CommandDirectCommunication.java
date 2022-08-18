/* (c) https://github.com/MontiCore/monticore */
package umlp.command;

import stanley.FlensburgTestContext;

public class CommandDirectCommunication implements CommandCommunication {

  protected FlensburgTestContext backendContext;
  // Wegen beidseitiger navigation muss dieser per setter gesetzt werden
  protected FlensburgTestContext clientContext;

  public CommandDirectCommunication(FlensburgTestContext backendContext) {
    this.backendContext = backendContext;
  }

  public void setClientContext(FlensburgTestContext clientContext) {
    this.clientContext = clientContext;
  }

  public long init() {
    long participantId = backendContext.getCommandManager()._subscribe(new CommandSubscription() {
      @Override
      public <T> void sendCommand(Command<T> command) {
        // TODO: Local error handling
        clientContext.activate();
        command.doRun();
        backendContext.activate();
      }
    });
    return participantId;
  }

  public <T> CommandResult<T> sendCommand(Command<T> command) {
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
}
