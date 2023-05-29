/* (c) https://github.com/MontiCore/monticore */

import { ClientCommandCommunication } from "../../command/ClientCommandCommunication";
import { Command } from "../../command/Command";
import { CommandObjectMapper } from "../../command/CommandObjectMapper";
import { CommandReceiveObserver } from "../../command/CommandReceiveObserver";
import { CommandResult } from "../../command/CommandResult";
import { CommandResultReceiveObserver } from "../../command/CommandResultReceiveObserver";
import { SimpleResult } from "../../command/SimpleResult";
import { LoadingState } from "../../common/LoadingState";
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { Result } from "../../common/Result";

export class ClientCommandWebsocketCommunication implements ClientCommandCommunication {
  public static MAX_TRIES: number = 10;
  private maxWaitingTime: number = 6000; // 6 seconds
  private participantId: number = null;
  // TODO AGe: any
  private session: WebSocketSubject<any>;
  // TODO AGe: URI
  private uri: string = 'ws://localhost:8000';

  // not necessary?
  // private headers: WebSocketHttpHeaders;

  private messages: Map<number, CommandResult<any>> = new Map();

  private commandObserver: Array<CommandReceiveObserver> = [];
  private commandResultObserver: Array<CommandResultReceiveObserver> = [];

  private socket: WebSocketSubject<any>;
  private established: boolean = false;

  /*
  executor: ExecutorService;
  private initLatch: number;
  /**/

  public ClientCommandWebsocketCommunication1(uri: string): ClientCommandWebsocketCommunication {
    new ClientCommandWebsocketCommunication().ClientCommandWebsocketCommunication2(uri/*, new WebSocketHttpHeaders()/**/);
    return this;
  }

  public ClientCommandWebsocketCommunication2(uri: string/*, headers: WebSocketHttpHeaders/**/) {
    this.uri = uri;
    // this.headers = headers;
    // this.executor = Executors.newFixedThreadPool(1);
  }

  public sendCommandToServerAndReceiveResult<T>(command: Command<T>): CommandResult<T> {
    command.setParticipantId(this.participantId);
    let result: CommandResult<T>;
    try {
      let payload: string = CommandObjectMapper.serializeCommand(command);
      console.info(`Sending message to server: ${payload}`, this.constructor.name);
      let sendingSuccessful: boolean = this.sendMessage(payload);
      if (!sendingSuccessful) {
        ClientCommandWebsocketCommunication.warnCouldNotReceive(command);
        return new SimpleResult<T>().SimpleResult2(
          command.getId(), LoadingState.FAILED_TO_LOAD, command.getCommandType()
        );
      }
      /*
      synchronized (this) {
        this.wait(maxWaitingTime);
      }
      /**/

      if (this.messages.has(command.getId())) {
        let response: CommandResult<any> = this.messages.get(command.getId());
        this.messages.delete(command.getId());
        // Stelle sicher, dass der Typ auch nach dem Deserialisieren gesichert ist
        // TODO AGe: what?
        /*
        if ((<ParameterizedType> command.getClass()
          .getGenericSuperclass()).getActualTypeArguments()[0].getTypeName()
          .equals(response.getTypeParameter())) { /**/
          result = <CommandResult<T>>response;
          console.info(`
            Received answer from server for command with id
            ${command.getId()}: ${CommandObjectMapper.serializeResponse(result)}
          `, this.constructor.name);
          /*
        }
        else {
          ClientCommandWebsocketCommunication.warnCouldNotReceive(command);
          return new SimpleResult<T>().SimpleResult2(command.getId(), LoadingState.FAILED_TO_LOAD, command.getCommandType());
        }
        /**/
      } else {
        ClientCommandWebsocketCommunication.warnCouldNotReceive(command);
        return new SimpleResult<T>().SimpleResult2(
          command.getId(), LoadingState.FAILED_TO_LOAD, command.getCommandType()
        );
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
  }

  private static warnCouldNotReceive<T>(command: Command<T>): void {
    console.warn("Could not receive response from server for command id: " + command.getId());
  }

  public init(): Result<number> {
    /*
    this.initLatch = new CountDownLatch(2);
    let webSocketClient: StandardWebSocketClient = new StandardWebSocketClient();
    try {
      this.session = WebSocketSessionManager.register(
        webSocketClient.doHandshake(this, this.headers, URI.create(uri)).get());
    }
    catch (e: InterruptedException) {
      console.warn(String.format("Could not establish a websocket connection to URI '%s'", uri));
      return Result.error();
    }

    try {
      // Warte bis die Verbindung aufgebaut ist und eine Antwort eintrifft
      let await: boolean = this.initLatch.await(2, TimeUnit.SECONDS);
      if (await && this.participantId != null) {
        return Result.ok(this.participantId);
      }
      else {
        return Result.error();
      }
    }
    catch (e: InterruptedException) {
      // e.printStackTrace();
      return Result.error();
    }
    /**/
    this.socket = webSocket(this.uri);
    this.socket.subscribe({
      next: msg => this.handleTextMessage(msg), 
      error: err => console.log(err), 
      complete: () => this.afterConnectionClosed() 
    })
    // TODO AGe: not 1
    return Result.ok(1);
  }

  // TODO AGe: any message
  protected handleTextMessage(message: any): void {
    if (this.expectInitMessage()) {
      // Fall initialisierung der Verbindung
      this.participantId = JSON.parse(message.getPayload());
      if (Number.isNaN(this.participantId)) {
        this.participantId = null;
      }
    }
    else {
      // Fall Antwort eines Requests, oder Erhalt eines weitergeleiteten Commands zur
      // Datensynchronisierung.
      // TODO AHe, MH: Wasteful way of distinguishing commands and results
      let commandResultOptional: CommandResult<any> = null;
      commandResultOptional = CommandObjectMapper.deserializeResponse(message.getPayload());
      let commandOptional: Command<Object> = null;
      commandOptional = CommandObjectMapper.deserializeCommand(message.getPayload());

      if (commandResultOptional != null) { // TODO MH: Need to check for participantId
        // Fall Antwort eines Requests
        let commandResult: CommandResult<any> = commandResultOptional;
        this.messages.set(commandResult.getCommandId(), commandResult);
        for (const observer of this.commandResultObserver) {
          observer(commandResult);
        }
        /*
        synchronized(this) {
          notify();
        }
        /**/
      }
      else if (commandOptional != null) {
        //Fall Antwort eines Erhalt eines weitergeleiteten Commands zur Datensynchronisierung
        let finalCommandOptional: Command<Object> = commandOptional;
        // this.executor.submit(() => {
          for (const consumer of this.commandObserver) {
            consumer(finalCommandOptional);
          }
        // });
      }
      else {
        /*
      synchronized(this) {
        notify();
      }
      /**/
      }
    }
  }

  protected expectInitMessage(): boolean {
    return !this.established;
    // return this.initLatch.getCount() > 0;
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

  public sendMessage(message: string): boolean  {
    let lastException: string = null;
    let tries: number = 0;

    // while (tries < ClientCommandWebsocketCommunication.MAX_TRIES) {
    this.socket.next({message: message});
    // }

    console.error("0xWS001 An error occured on sending a websocket message", lastException);
    return false;
  }
}
