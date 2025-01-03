/* (c) https://github.com/MontiCore/monticore */
import {CommandManager} from '../command/CommandManager';

export class LogUtil {
  constructor() {
  }

  public static getLoggerName(): string {
    const participantId: number = CommandManager.getParticipantId();
    return participantId === 0 ? "Server" : "Client " + participantId;
  }
}

LogUtil["__class"] = "umlp.backendrte.util.LogUtil";



