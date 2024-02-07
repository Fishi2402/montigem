/* (c) https://github.com/MontiCore/monticore */

import {CommandManager} from "./CommandManager";
import {ClientCommandCommunication} from "./ClientCommandCommunication";
import {Command} from "./Command";
import {CommandResult} from "./CommandResult";

export class ClientCommandManager extends CommandManager {

  private initSuccessFul: boolean = false;

  protected commandCommunication: ClientCommandCommunication;

  public constructor(commandCommunication: ClientCommandCommunication) {
    super();
    this.commandCommunication = commandCommunication.onCommandReceived(cmd => cmd.doRun());
    this.initCommunication();
  }

  public initCommunication() {
    let promiseResolve: (value: unknown) => void, promiseReject: (reason?: any) => void;

    const retPromise = new Promise(function(resolve, reject){
      promiseResolve = resolve;
      promiseReject = reject;
    });

    this.commandCommunication.init().then(res => {
      let initResult: number = res;
      if (!Number.isNaN(initResult)) {
        this.participantId = initResult;
        this.initSuccessFul = true;
        promiseResolve(this.participantId);
      } else {
        promiseReject();
        // TODO MH
      }
    })
    return retPromise;
  }

  protected override async _executeCommand<T>(command: Command<T>): Promise<CommandResult<T>> {
    command.setParticipantId(this.participantId);
    return await this.commandCommunication.sendCommandToServerAndReceiveResult(command);
  }

  public isInitSuccessFul(): boolean {
    return this.initSuccessFul;
  }
}
