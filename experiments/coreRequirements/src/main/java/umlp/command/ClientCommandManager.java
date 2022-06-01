package umlp.command;

// RTE4T

import hardy.FlensburgManager;

// Simulation des CommandManagers im Client.
// Aktuell werden zum Ausführen eines Commands der Backend-Kontext wiederhergestellt und der Command dann ausgeführt.
// Dadurch wird die (De-)serialisierung nicht genutzt.
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

    // Restore FlensburgManager and CommandManager to client
    FlensburgManager.initMe(clientManager);
    instance = this;
    return res;
  }
}
