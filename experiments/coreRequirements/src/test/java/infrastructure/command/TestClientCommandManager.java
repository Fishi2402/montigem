/* (c) https://github.com/MontiCore/monticore */
package infrastructure.command;

// RTE4T

import umlp.command.*;
import umlp.rte.Result;

// Simulation des CommandManagers im Client.
// Es muss immer eine ClientCommandCommunication mit übergeben werden. Diese sorgt dann für den Transport
// der Commands zwischen Client und Server.
// Das sorgt dafür, dass die Kommunikation austauschbar bleibt.
public class TestClientCommandManager extends CommandManager {

  // Die ClientCommandCommunication sorgt für den Transport von Commands zwischen Client und Server
  protected ClientCommandCommunication commandCommunication;

  public TestClientCommandManager(ClientCommandCommunication commandCommunication){
    this.commandCommunication = commandCommunication;
    Result<Long> init = commandCommunication.init();
    if (init.wasSuccessful()) {
      participantId = init.getValue();
    } else {
      // TODO MH
    }
  }

  @Override
  protected <T> CommandResult<T> _executeCommand(Command<T> command) {
    return commandCommunication.sendCommandToServerAndReceiveResult(command);
  }
}
