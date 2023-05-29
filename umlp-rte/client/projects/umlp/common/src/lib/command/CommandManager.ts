/* (c) https://github.com/MontiCore/monticore */

import { LogUtil } from "../util/LogUtil";
import { Command } from "./Command";
import { CommandObjectMapper } from "./CommandObjectMapper";
import { CommandResult } from "./CommandResult";
import { Participant } from "./Participant";

export class CommandManager {
  // Id dieses Clients/Servers
  // Server ist immer 0
  protected participantId: number = 0;

  protected CommandManager() { }
  // Speichert alle verbundenen Clients und deren Ids, wird genutzt um die Commands die relevante objekte bearbeiten
  // weiter zu verteilen.
  protected particpants: Map<number, Participant> = new Map();
  protected runningClientId: number = 1;

  protected static instance: CommandManager;

  public static register(participant: Participant): number {
    return CommandManager.getInstance()._register(participant);
  }

  public static deregister(participantId: number): void {
    CommandManager.getInstance()._deregister(participantId);
  }

  public static getParticipantId(): number {
    return CommandManager.getInstance()._getParticipantId();
  }

  protected _getParticipantId(): number {
    return this.participantId;
  }

  public _register(participant: Participant): number {
    let participantId: number = this.runningClientId++;
    this.particpants.set(participantId, participant);
    return participantId;
  }

  protected _deregister(participantId: number): void {
    this.particpants.delete(participantId);
  }


  public static init(): void {
    this.instance = new CommandManager();
  }

  public static initMe(commandManager: CommandManager): void {
    this.instance = commandManager;
  }

  public static getInstance(): CommandManager {
    if (this.instance == null) {
      this.instance = new CommandManager();
    }
    return this.instance;
  }

  public static executeCommand<T>(command: Command<T>): CommandResult<T> {
    console.trace("Exec command " + command.getId() + " " + command.getCommandType() + " from " + command.getParticipantId(), LogUtil.getLoggerName());
    console.trace(CommandObjectMapper.serializeCommand(command), LogUtil.getLoggerName());
    return CommandManager.getInstance()._executeCommand(command);
  }

  protected _executeCommand<T>(command: Command<T>): CommandResult<T> {
    let res: CommandResult<T> = command.doRun();
    // update subscribed gemIds
    if (this.particpants.has(command.getParticipantId())) {
      this.particpants.get(command.getParticipantId()).subscribe(command.getNewlySubscribedGemIds());
    } else {
      // TODO: MH: kann das passieren?
    }
    // TODO: error handling if doRun fails
    if (!command.isRequest()) {
      this._sendCommandToSubscribers(command);
    }
    return res;
  }

  public static sendCommandToSubscribers<T>(command: Command<T>): void {
    CommandManager.getInstance()._sendCommandToSubscribers(command);
  }

  protected _sendCommandToSubscribers<T>(command: Command<T>): void {
    for (const participantId of this.particpants.keys()) {
      let participant: Participant = this.particpants.get(participantId);
      if (!(participantId === command.getParticipantId())) {
        let match: boolean = false;
        for (const gemId of command.getEffectedGemIds()) {
          if (participant.isSubscribedTo(gemId)) {
            match = true;
            break;
          }
        }
        if (match) {
          console.trace("Sending command " + command.getId() + " " + command.getCommandType() + " to " + participantId, LogUtil.getLoggerName());
          participant.sendCommand(command);
        } else {
          console.trace("Skip sending command " + command.getCommandType() + " to " + participantId, LogUtil.getLoggerName());
        }
      }
    }
  }

  protected freeCommandId: number;
  public static getFreeCommandId(): number {
    return CommandManager.getInstance()._getFreeCommandId();
  }

  protected _getFreeCommandId(): number {
    return this.freeCommandId++;
  }
}
