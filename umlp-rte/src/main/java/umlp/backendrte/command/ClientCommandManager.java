/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.command;

import umlp.backendrte.common.Result;

public class ClientCommandManager extends CommandManager {

  private Long participantId = 0L;

  protected ClientCommandCommunication commandCommunication;

  public ClientCommandManager(ClientCommandCommunication commandCommunication){
    this.commandCommunication = commandCommunication.onCommandReceived(cmd -> cmd.doRun());
    Result<Long> initResult = commandCommunication.init();
    if (initResult.wasSuccessful()) {
      participantId = initResult.getValue();
    } else {
      // TODO MH
    }
  }

  @Override
  protected <T> CommandResult<T> _executeCommand(Command<T> command) {
    return commandCommunication.sendCommandToServerAndReceiveResult(command);
  }
}
