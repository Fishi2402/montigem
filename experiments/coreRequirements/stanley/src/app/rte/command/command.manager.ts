/* (c) https://github.com/MontiCore/monticore */

// RTE

import {ClientWebsocketCommandCommunication} from "./client.websocket.command.communication";
import {Command} from "./command";
import {CommandResult} from "./command.result";

export interface ClientCommandCommunication {
  sendCommandToServerAndReceiveResult: <T> (command: Command<T>) => Promise<CommandResult<T>>;
  init: () => Promise<number>;
}

export interface CommandCallback<T> {
  resolveCallback: (val: CommandResult<T>) => void;
  rejectCallback: (val: CommandResult<T>) => void;
}

function defaultClientCommandCommunication(): ClientCommandCommunication {
  let uri = "ws://localhost";
  let port = 8081;
  return new ClientWebsocketCommandCommunication(uri, port);
}

export class CommandManager {
  // Id dieses Clients
  protected participantId: number = 0;
  protected clientCommandCommunication: ClientCommandCommunication;

  constructor(clientCommandCommunication?: ClientCommandCommunication) {
    if (clientCommandCommunication) {
      this.clientCommandCommunication = clientCommandCommunication;
    } else {
      this.clientCommandCommunication = defaultClientCommandCommunication();
    }
  }

  protected static instance: CommandManager;

  public static getInstance(): CommandManager {
    if(CommandManager.instance == null){
      CommandManager.instance = new CommandManager();
    }
    return CommandManager.instance;
  }

  public static getParticipantId(): number {
    return CommandManager.getInstance()._getParticipantId();
  }

  protected _getParticipantId(): number {
    return this.participantId;
  }

  public static executeCommand<T> (command: Command<T>): Promise<CommandResult<T>> {
  // Log.trace("Exec command " + command.getId() + " " + command.getCommandType() + " from " + command.getParticipantId(), LogUtil.getLoggerName());
  return CommandManager.getInstance()._executeCommand(command);
}

  protected _executeCommand<T> (command: Command<T>): Promise<CommandResult<T>>  {
    return this.clientCommandCommunication.sendCommandToServerAndReceiveResult(command);
  }

  // TODO
  public static getFreeCommandId(): number {
    return CommandManager.getInstance()._getFreeCommandId();
  }

  // TODO
  protected _getFreeCommandId(): number {
    return 0;
  }
}
