package umlp.command;

// RTE4T

import stanley.FlensburgTestContext;

// Simulation des CommandManagers im Client.
// Aktuell werden zum Ausführen eines Commands der Backend-Kontext wiederhergestellt,
// und der Command ausgeführt.
// Das result wird serialisiert und dann deserialisiert bevor es an den Client weitergeleitet wird.
// Die (de)-serialisierung sorgt dafür, dass unterschiedliche Instanzen der verschickten Objekte erstellt werden,
// wodurch sich die Datensynchronisierung testen lässt.
public class TestClientCommandManager extends CommandManager {
  protected FlensburgTestContext backendContext;
  // Wegen beidseitiger navigation muss dieser per setter gesetzt werden
  protected FlensburgTestContext clientContext;

  public TestClientCommandManager(FlensburgTestContext backendContext){
    this.backendContext = backendContext;
    participantId = backendContext.getCommandManager()._subscribe(new CommandSubscription() {
      @Override
      public <T> void sendCommand(Command<T> command) {
        // TODO: Local error handling
        clientContext.activate();
        command.doRun();
        backendContext.activate();
      }
    });
  }

  public FlensburgTestContext getClientContext() {
    return clientContext;
  }

  public void setClientContext(FlensburgTestContext clientContext) {
    this.clientContext = clientContext;
  }

  @Override
  protected <T> CommandResult<T> _executeCommand(Command<T> command) {
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
