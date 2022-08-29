package hardy.commands;

// RTE4T

import stanley.FlensburgTestContext;
import umlp.command.*;

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

  protected CommandCommunication commandCommunication;

  public TestClientCommandManager(CommandCommunication commandCommunication){
    this.commandCommunication = commandCommunication;
    participantId = backendContext.getCommandManager()._register(new Participant(
        new CommandDirectCommunication(backendContext)));
  }

  @Override
  protected <T> CommandResult<T> _executeCommand(Command<T> command) {
    return commandCommunication.sendCommandAndReceiveResult(command);
  }
}
