/* (c) https://github.com/MontiCore/monticore */

import {ClientCommandCommunication} from "./ClientCommandCommunication";
import {Command} from "./Command";
import {CommandManager} from "./CommandManager";
import {CommandResult} from "./CommandResult";

export class ClientCommandManager extends CommandManager {

  private initSuccessFul: boolean = false;

  protected commandCommunication: ClientCommandCommunication;

  // A lock/mutex on this command manager state,
  // to ensure, that commands are run in the order they arrive in
  protected commandMutex: CommandMutex;

  public constructor(commandCommunication: ClientCommandCommunication) {
    super();
    this.commandMutex = new CommandMutex();
    this.commandCommunication = commandCommunication.onCommandReceived(cmd => {
      this.commandMutex.acquireLock().then(releaseLock => {
        cmd.doRun().finally(() => {
          releaseLock(); // Release the lock again
        })
      });

    });
    commandCommunication.init().then(res => {
      let initResult: number = res;
      if (!Number.isNaN(initResult)) {
        this.participantId = initResult;
        this.initSuccessFul = true;
      } else {
        // TODO MH
      }
    })
  }

  public retryInitCommunicationImmediate() {
    this.commandCommunication.retryImmediate();
  }

  public pauseRetryCommunication() {
    this.commandCommunication.pauseRetry();
  }

  protected override _executeCommand<T>(command: Command<T>): Promise<CommandResult<T>> {
    command.setParticipantId(this.participantId);
    return this.commandCommunication.sendCommandToServerAndReceiveResult(command);
  }

  public isInitSuccessFul(): boolean {
    return this.initSuccessFul;
  }

  // A chained-queue like data structure
  private globalLock: {
    waitFor: (resolve: () => void) => void
  } = {
    "waitFor": (r) => r()
  };

}

// The mutex is required due to https://git.rwth-aachen.de/monticore/monticore/-/issues/4378
class CommandMutex {
  private _queue: {
    resolve: (release: () => void) => void
  }[] = [];

  private _locked = false;

  /**
   * Acquire a new lock on a resource (the global state of the command manager).
   *
   * The promised callback MUST be called to indicate, that the resource is free again.
   *
   */
  acquireLock() {
    return new Promise<() => void>((resolve) => {
      this._queue.push({resolve});
      this._dispatch();
    });
  }

  private _dispatch(ignoreLocked = false) {
    if (this._locked || ignoreLocked) return; // Do nothing, as we are still waiting - the release function will invoke dispatch again
    this._locked = true;
    const nextCommand = this._queue.shift();
    if (!nextCommand) {
      this._locked = false;
      return;
    }

    // We have a new contender and the lock is available
    this._locked = true; // Lock the lock
    // and give the next command access
    nextCommand.resolve(() => {
      // Once the command is done, this callback MUST be invoked
      this._dispatch(true);
    });
  }

}
