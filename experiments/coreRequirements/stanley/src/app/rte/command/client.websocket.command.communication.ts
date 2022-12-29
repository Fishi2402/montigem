/* (c) https://github.com/MontiCore/monticore */
import {ClientCommandCommunication, CommandCallback} from "./command.manager";
import {Command} from "./command";
import {CommandResult} from "./command.result";
import {SimpleResult} from "./simple.result";
import {Websocket, WebsocketBuilder} from 'websocket-ts';
import {Observable} from "rxjs";
import {StatusResult} from "./status.result";
import {LoadingState} from "./loading.state";


export class ClientWebsocketCommandCommunication implements ClientCommandCommunication {

  protected commandCallbackMap: Map<number, CommandCallback<any>> = new Map();

  protected uri: string;

  private participantId: number = 0;

  private ws: Websocket | undefined;

  constructor(uri: string, port: number) {
    this.uri = uri + ":" + port + "/umlp/api/command";
  }

  public init(): Promise<number> {
    this.ws = new WebsocketBuilder(this.uri)
      .onOpen((i, ev) => {
        console.log("onopen");
      }).onClose((i, ev) => {
        console.log("closed");
      }).onError((i, ev) => {
        console.log("error");
      }).onMessage((i, ev) => {
        console.log("message");
        // TODO try catch parse undso, Error parsen und rejecten
        let parse = JSON.parse(ev.data);
        let id = parse.getId();
        if (this.commandCallbackMap.has(id)) {
          let commandCallback = this.commandCallbackMap.get(id);
          if (commandCallback) {
            commandCallback.resolveCallback(parse);
          }
        } else {
          // TODO
        }
      }).onRetry((i, ev) => {
        console.log("retry");
      }).build();


    // TODO checken ob in "onOpen" oder in "onMessage" die initiale ClientID zurückgegeben wird
    return new Promise<number>((resolve, reject) => {
      resolve(1);
    });
  }

  public async sendCommandToServerAndReceiveResult<T>(command: Command<T>): Promise<CommandResult<T>> {
    if (this.ws) {
      this.ws.send(JSON.stringify(command));
    } else {

      return SimpleResult.failedToLoad(command.getId(), command.getCommandType());
    }

    return new Promise<CommandResult<T>>((resolve, reject) => {
      this.commandCallbackMap.set(command.getId(), {
        resolveCallback: (val: CommandResult<T>) => {
          // TODO log
          resolve(val);
        },
        rejectCallback: (val: CommandResult<T>) => {
          // TODO log
          reject(val);
        }
      })
    });
  }

}
