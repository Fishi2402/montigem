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
import {
  first,
  firstValueFrom,
  from,
  retry,
  Subject,
  take,
  timer,
  BehaviorSubject,
  tap,
  filter,
  bufferWhen,
  merge, ObservableInput, Observable, combineLatestAll, combineLatest, bufferToggle, buffer, map
} from "rxjs";

type QueueProcessResult = {
  processedTicket: number;
  resultProcessCommand: CommandResult<any>;
};

type QueuePosition = {
  waitingTicket : number;
  command: Command<any>;
}

export class ClientCommandWebsocketCommunication implements ClientCommandCommunication {
  public static MAX_TRIES: number = 10;
  private maxWaitingTime: number = 6000; // 6 seconds
  private participantId: number = -1;

  private participantIdSubject: BehaviorSubject<number>= new BehaviorSubject<number>(-1);
  // TODO AGe: any
  private session: WebSocketSubject<any>;
  // TODO AGe: URI
  private uri: string = 'ws://localhost:8000';

  // not necessary?
  // private headers: WebSocketHttpHeaders;

  private commandQueue : BehaviorSubject<QueuePosition>
    = new BehaviorSubject({waitingTicket: 0, command: undefined});
  private processedCommandsQueue : BehaviorSubject<QueueProcessResult>
    = new BehaviorSubject<QueueProcessResult>({processedTicket:0, resultProcessCommand:undefined})

  private messages: Map<number, Subject<CommandResult<any>>> = new Map();

  private commandObserver: Array<CommandReceiveObserver> = [];
  private commandResultObserver: Array<CommandResultReceiveObserver> = [];

  private socket: WebSocketSubject<any>;
  private outgoingMessages = new Subject<any>();
  private connectionStatus = new BehaviorSubject<boolean>(false);
  private established: boolean = false;

  private immediateRetry: Subject<any> = new Subject();
  private retryCount: number = 0;

  /*
  executor: ExecutorService;
  private initLatch: number;
  /**/

  public constructor(uri: string) {
    this.uri = uri;
  }

  public sendCommandToServerAndReceiveResult<T>(command: Command<T>): Promise<CommandResult<T>> {

    // Draw a waiting ticket
    const currentWaitingNumber = this.commandQueue.value.waitingTicket +1;

    // queue up in line -> this will possibly be buffered if we are waiting for a participantId from the server
    const positionAndCommand: QueuePosition = {
      waitingTicket : currentWaitingNumber,
      command: command
    }

    // Nur hier darf man sich anstellen!
    console.debug("Add command to queue:", currentWaitingNumber, command, positionAndCommand)
    this.commandQueue.next(positionAndCommand)

    // Wait until your ticket was processed, i.e. Command was executed waitingTicket == processedTicket
    const nextOneIsOurCommand = firstValueFrom(this.processedCommandsQueue.pipe(
      filter(queuePlace => queuePlace.processedTicket===currentWaitingNumber)))
      .then(commandQueuePlace => {
        console.debug("Received result of command", currentWaitingNumber)
        return commandQueuePlace.resultProcessCommand
      }
    )
    return nextOneIsOurCommand
  }

  /**
   * We need a Command Queue, so we can buffer commands while waiting for our participant ID
   * @private
   */
  private subscribeToCommandQueue() {
    console.debug("Initial subscription to command buffer.");

    // This observable emits a value when a new command is sent, ONLY when the participantId at this time is not -1
    // And also when the participantId gets updated i.e. we established a connection
    const commandsCanBeBeSent =
      combineLatest([
        this.participantIdSubject,
        this.commandQueue]
      ).pipe(filter(val => val[0]!=-1 || this.participantId!==-1))

    this.commandQueue.pipe(
      // Buffer Commands as long as they cannot be sent
      buffer(commandsCanBeBeSent)
    ).subscribe(commandsBuffer => {
      console.debug("Clearing Command Buffer", commandsBuffer)
      // Take a Command execute(process) it and put the result into the processedCommandsQueue
      commandsBuffer.forEach(queuePos => {

        // First command is undefined, and we do not want to send this
        if(queuePos.command) {
          console.debug("Sending command from buffer", queuePos.command)
          this.processIndividualCommand(queuePos.command).then(res => {
            this.processedCommandsQueue.next({
              processedTicket:  queuePos.waitingTicket,
              resultProcessCommand: res
            })
          })
        }
      })
    })
  }

  private processIndividualCommand<T>(command : Command<T>) {
    command.setParticipantId(this.participantId);
    // let result: CommandResult<T>;
    // try {
    let payload: string = CommandObjectMapper.serializeCommand(command);
    console.info(`Sending message to server: ${payload}`, this.constructor.name);
    let sendingSuccessful: boolean = this.sendMessage(payload);
    if (!sendingSuccessful) {
      ClientCommandWebsocketCommunication.warnCouldNotReceive(command);
      return Promise.resolve(new SimpleResult<T>().SimpleResult2(
        command.getId(), LoadingState.FAILED_TO_LOAD, command.getCommandType()
      ));
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
  }

  private static warnCouldNotReceive<T>(command: Command<T>): void {
    console.warn("Could not receive response from server for command id: " + command.getId());
  }

  public init(): Promise<number> {
    this.subscribeToCommandQueue()

    this.socket = webSocket({
      url: this.uri,
      openObserver: {
        next: () => this.connectionStatus.next(true)
      },
      closeObserver: {
        next: () => this.connectionStatus.next(false)
      }
    });
    const retrySocket = this.socket.pipe(
      retry({ // retry, but with back off of 2^n * backoffInterval (and at most 10s)
        delay: (_, retryCount:number) => {
          if (this.retryCount == -2) {
            // disabled
            return this.immediateRetry;
          }
          this.retryCount++;
          if (retryCount < this.retryCount)
            this.retryCount = retryCount;
          const backoffInterval = 400;
          return merge(this.immediateRetry, timer(Math.min(10*1000, Math.pow(2, this.retryCount - 1) * backoffInterval)));
        },
        resetOnSuccess: true,
      })
    );

    // Outgoing message queue
    // Buffer messages when WebSocket is not connected, flushed when it reconnects
    const bufferedMessages$ = this.outgoingMessages.pipe(
      filter( () => !this.connectionStatus.value), // only if not connected
      bufferWhen(() => this.connectionStatus.pipe(filter(isOpen => isOpen))) // flush them when it reconnects
    );

    // Subscription to send messages through the directly WebSocket only if it's open
    this.outgoingMessages.pipe(
      filter(() => this.connectionStatus.value), // Only if open
      tap(message => {
        this.socket.next(message);
      })
    ).subscribe();
    // Subscribe to flush buffered messages when connection is re-established
    bufferedMessages$.subscribe(messages => {
      messages.forEach(message => {
        this.socket.next(message);
      });
    });


    retrySocket.subscribe({
      next: msg => this.handleTextMessage(msg),
      error: err => console.log(err),
      complete: () => this.afterConnectionClosed()
    });

    return firstValueFrom(retrySocket);
  }

  // TODO AGe: any message
  protected handleTextMessage(message: any): void {
    if (this.expectInitMessage()) {
      this.established = true;
      // Fall initialisierung der Verbindung
      console.warn("Connection established")
      this.participantId = JSON.parse(message);
      if (Number.isNaN(this.participantId)) {
        this.participantId = null;
      } else {
        this.participantIdSubject.next(this.participantId)
      }
    }
    else {
      // Fall Antwort eines Requests, oder Erhalt eines weitergeleiteten Commands zur
      // Datensynchronisierung.
      // TODO AHe, MH: Wasteful way of distinguishing commands and results
      // TODO AGe: what?
      const payload = JSON.stringify(message);
      let commandResultOptional: CommandResult<any> = null;
      commandResultOptional = CommandObjectMapper.deserializeResponse(payload);
      let commandOptional: Command<Object> = null;
      commandOptional = CommandObjectMapper.deserializeCommand(payload);

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
          observer(commandResult);
        }
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
    // TODO AGe: what?
    let payload = JSON.parse(message);
    this.outgoingMessages.next(payload);
    // }

    return true;
    // TODO: identify unsuccessful
    /*
    console.error("0xWS001 An error occured on sending a websocket message", lastException);
    return false;
    /**/
  }

  public retryImmediate() {
    this.retryCount = 0;
    this.immediateRetry.next(true);
  }

  public pauseRetry() {
    this.retryCount = -2;
  }
}
