import {ClientCommandCommunication} from "./command.manager";
import {Command} from "./command";
import {CommandResult} from "./command.result";
import {SimpleResult} from "./simple.result";


export class ClientWebsocketCommandCommunication implements ClientCommandCommunication {

  protected port: number;
  protected uri: string;

  constructor(uri: string, port: number) {
    this.uri = uri;
    this.port = port;
  }

  public init(): number {
    return 0;
  }

  public sendCommandToServerAndReceiveResult<T> (command: Command<T>): CommandResult<T> {
    return new SimpleResult(0, "");
  }

}
