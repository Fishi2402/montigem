/* (c) https://github.com/MontiCore/monticore */

import { Result } from "../common/Result";
import { ClientCommandCommunication } from "./ClientCommandCommunication";
import { Command } from "./Command";
import { CommandManager } from "./CommandManager";
import { CommandResult } from "./CommandResult";

export class ClientCommandManager extends CommandManager {

  private initSuccessFul: boolean = false;

  protected commandCommunication: ClientCommandCommunication;

  public ClientCommandManager(commandCommunication: ClientCommandCommunication){
    this.commandCommunication = commandCommunication.onCommandReceived(cmd => cmd.doRun());
    let initResult: Result<number> = commandCommunication.init();
    if (initResult.wasSuccessful()) {
      this.participantId = initResult.getValue();
      this.initSuccessFul = true;
    } else {
      // TODO MH
    }
  }

  protected override _executeCommand<T>(command: Command<T>): CommandResult<T>  {
    command.setParticipantId(this.participantId);
    return this.commandCommunication.sendCommandToServerAndReceiveResult(command);
  }

  protected override _sendCommandToSubscribers<T> (command: Command<T>): void  {
    this._executeCommand(command);
  }

  public isInitSuccessFul(): boolean  {
    return this.initSuccessFul;
  }
}
