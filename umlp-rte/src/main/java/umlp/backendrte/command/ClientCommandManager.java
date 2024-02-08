/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.command;

import umlp.backendrte.common.Result;

public class ClientCommandManager extends CommandManager {

  private boolean initSuccessFul = false;

  protected ClientCommandCommunication commandCommunication;

  public ClientCommandManager(ClientCommandCommunication commandCommunication){
    this.commandCommunication = commandCommunication.onCommandReceived(cmd -> cmd.doRun());
    this.initCommunication();
  }

  public void initCommunication() {
    Result<Long> initResult = commandCommunication.init();
    if (initResult.wasSuccessful()) {
      participantId = initResult.getValue();
      initSuccessFul = true;
    } else {
      // TODO MH
    }
  }

  @Override
  protected <T> CommandResult<T> _executeCommand(Command<T> command) {
    command.setParticipantId(participantId);
    return commandCommunication.sendCommandToServerAndReceiveResult(command);
  }

  @Override
  protected <T> void _sendCommandToSubscribers(Command<T> command) {
    _executeCommand(command);
  }

  public boolean isInitSuccessFul() {
    return initSuccessFul;
  }
}
