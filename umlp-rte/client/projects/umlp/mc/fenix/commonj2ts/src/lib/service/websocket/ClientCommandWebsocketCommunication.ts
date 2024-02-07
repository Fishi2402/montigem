/* (c) https://github.com/MontiCore/monticore */

import {ClientCommandCommunication} from "../../command/ClientCommandCommunication";
import {Command} from "../../command/Command";
import {CommandObjectMapper} from "../../command/CommandObjectMapper";
import {CommandReceiveObserver} from "../../command/CommandReceiveObserver";
import {CommandResult} from "../../command/CommandResult";
import {CommandResultReceiveObserver} from "../../command/CommandResultReceiveObserver";
import {SimpleResult} from "../../command/SimpleResult";
import {LoadingState} from "../../common/LoadingState";
import {webSocket, WebSocketSubject} from 'rxjs/webSocket';
import {first, firstValueFrom, from, Subject, take} from "rxjs";

export class ClientCommandWebsocketCommunication implements ClientCommandCommunication {
  public static MAX_TRIES: number = 10;
  private maxWaitingTime: number = 6000; // 6 seconds
  private participantId: number = -1;
  // TODO AGe: any
  private session: WebSocketSubject<any>;
  // TODO AGe: URI
  private uri: string = 'ws://localhost:8000';

  // not necessary?
  // private headers: WebSocketHttpHeaders;

  private messages: Map<number, Subject<CommandResult<any>>> = new Map();

  private commandObserver: Array<CommandReceiveObserver> = [];
  private commandResultObserver: Array<CommandResultReceiveObserver> = [];

  private socket: WebSocketSubject<any>;
  private established: boolean = false;

  /*
  executor: ExecutorService;
  private initLatch: number;
  /**/

  public constructor(uri: string) {
    this.uri = uri;
  }

  private static warnCouldNotReceive<T>(command: Command<T>): void {
    console.warn("Could not receive response from server for command id: " + command.getId());
  }

  public async sendCommandToServerAndReceiveResult<T>(command: Command<T>): Promise<CommandResult<T>> {
    command.setParticipantId(this.participantId);
    // let result: CommandResult<T>;
    // try {
    let payload: string = await CommandObjectMapper.serializeCommand(command);
    console.info(`Sending message to server: ${payload}`, this.constructor.name);
    let sendingSuccessful: boolean = this.sendMessage(payload);
    if (!sendingSuccessful) {
      ClientCommandWebsocketCommunication.warnCouldNotReceive(command);
      return Promise.resolve(new SimpleResult<T>().createFromLoadingState(command.getId(), LoadingState.FAILED_TO_LOAD, command.getCommandType()));
    }
    const resultSubject = new Subject<CommandResult<T>>();
    this.messages.set(command.getId(), resultSubject);
    const result = new Promise<CommandResult<T>>((resolve, reject) => {
      from(resultSubject).pipe(first(), take(1)).subscribe(v => {
        resolve(v);
        /*
        resolve(new SimpleResult<T>().SimpleResult1(
            command.getId(), v, command.getCommandType()
        ));
        /**/
      });
      // TODO AGe when reject?
    });
    result.then(() => this.messages.delete(command.getId()));

    return result;

    /*

    if (this.messages.has(command.getId())) {
      let response: Promise<CommandResult<any>> = this.messages.get(command.getId());
      response
      .then(value => {
        this.messages.delete(command.getId());
        result = <CommandResult<T>>value;
        console.info(`
          Received answer from server for command with id
          ${command.getId()}: ${CommandObjectMapper.serializeResponse(result)}
        `, this.constructor.name);
      })
      .catch(() => {
        ClientCommandWebsocketCommunication.warnCouldNotReceive(command);
        return new SimpleResult<T>().SimpleResult2(
          command.getId(), LoadingState.FAILED_TO_LOAD, command.getCommandType()
        );
      });
    }
  }
  catch (e: any) {
    ClientCommandWebsocketCommunication.warnCouldNotReceive(command);
    // e.printStackTrace();
    return new SimpleResult<T>().SimpleResult2(
      command.getId(), LoadingState.FAILED_TO_LOAD, command.getCommandType()
    );
  }
  return result;
  /**/
  }

  public init(): Promise<number> {
    this.socket = webSocket(this.uri);
    this.socket.subscribe({
      next: msg => this.handleTextMessage(msg),
      error: err => console.log(err),
      complete: () => this.afterConnectionClosed()
    });

    return firstValueFrom(this.socket);
  }

  public onCommandReceived(observer: CommandReceiveObserver): ClientCommandWebsocketCommunication {
    this.commandObserver.push(observer);
    return this;
  }

  public onCommandResultReceived(observer: CommandResultReceiveObserver): ClientCommandWebsocketCommunication {
    this.commandResultObserver.push(observer);
    return this;
  }

  public isOpen(): boolean {
    return this.socket != null;
  }

  public afterConnectionClosed(): void {
    console.warn(`Websocket connection closed`);
    this.socket.unsubscribe();
  }

  public handleTransportError(exception: any): void {
    console.warn(`Websocket transportation error ${exception}`);
  }

  public sendMessage(message: string): boolean {
    let lastException: string = null;
    let tries: number = 0;

    // while (tries < ClientCommandWebsocketCommunication.MAX_TRIES) {
    // TODO AGe: what?
    let payload = JSON.parse(message);
    this.socket.next(payload);
    // }

    return true;
    // TODO: identify unsuccessful
    /*
    console.error("0xWS001 An error occured on sending a websocket message", lastException);
    return false;
    /**/
  }

  // TODO AGe: any message
  protected async handleTextMessage(message: any): Promise<void> {
    if (this.expectInitMessage()) {
      this.established = true;
      // Fall initialisierung der Verbindung
      this.participantId = JSON.parse(message);
      if (Number.isNaN(this.participantId)) {
        this.participantId = null;
      }
    } else {
      // Fall Antwort eines Requests, oder Erhalt eines weitergeleiteten Commands zur
      // Datensynchronisierung.
      // TODO AHe, MH: Wasteful way of distinguishing commands and results
      // TODO AGe: what?
      const payload = JSON.stringify(message);
      let commandResultOptional: CommandResult<any> = null;
      commandResultOptional = await CommandObjectMapper.deserializeResponse(payload);
      let commandOptional: Command<Object> = null;
      commandOptional = await CommandObjectMapper.deserializeCommand(payload);

      if (commandResultOptional != null) { // TODO MH: Need to check for participantId
        // Fall Antwort eines Requests
        let commandResult: CommandResult<any> = commandResultOptional;
        if (this.messages.has(commandResult.getCommandId())) {
          this.messages.get(commandResult.getCommandId()).next(commandResult);
        } else {
          // TODO AGe: received some rogue command?
        }
        // this.messages.set(commandResult.getCommandId(), commandResult);
        for (const observer of this.commandResultObserver) {
          // observer(commandResult);
        }
      } else if (commandOptional != null) {
        //Fall Antwort eines Erhalt eines weitergeleiteten Commands zur Datensynchronisierung
        let finalCommandOptional: Command<Object> = commandOptional;
        // this.executor.submit(() => {
        for (const consumer of this.commandObserver) {
          // consumer(finalCommandOptional);
        }
        // });
      }
    }
  }

  protected expectInitMessage(): boolean {
    return !this.established;
    // return this.initLatch.getCount() > 0;
  }
}
