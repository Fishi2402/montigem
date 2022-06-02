package umlp.command;

// RTE4T

import hardy.FlensburgManager;

// Simulation des CommandManagers im Client.
// Aktuell werden zum Ausführen eines Commands der Backend-Kontext wiederhergestellt,
// und der Command ausgeführt.
// Das result wird serialisiert und dann deserialisiert bevor es an den Client weitergeleitet wird.
// Die (de)-serialisierung sorgt dafür, dass unterschiedliche Instanzen der verschickten Objekte erstellt werden,
// wodurch sich die Datensynchronisierung testen lässt.
public class ClientCommandManager extends CommandManager{
  protected FlensburgManager backendManager;

  public ClientCommandManager(FlensburgManager backendManager){
    this.backendManager = backendManager;
  }

  @Override
  protected <T> CommandResult<T> _executeSingleCommand(Command<T> command) {
    // Set FlensburgManager and CommandManager to backend
    FlensburgManager clientManager = FlensburgManager.getSingleton();
    FlensburgManager.initMe(backendManager);
    CommandManager.init();

    // Exec command
    CommandResult<T> res = CommandManager.executeSingleCommand(command);
    // Use (de)-serialization to simulate Communication
    String resStr = CommandObjectMapper.serializeResponse(res);
    CommandResult<?> recreatedRes = CommandObjectMapper.deserializeResponse(resStr);


    // Restore FlensburgManager and CommandManager to client
    FlensburgManager.initMe(clientManager);
    instance = this;
    return (CommandResult<T>) recreatedRes;
  }
}
