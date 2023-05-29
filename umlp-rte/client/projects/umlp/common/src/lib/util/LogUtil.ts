/* (c) https://github.com/MontiCore/monticore */

import { CommandManager } from "../command/CommandManager";

// RTE-P
// RTE-T

export class LogUtil {
  private constructor() {}

  public static getLoggerName(): String {
    let participantId: number = CommandManager.getParticipantId();
    return participantId == 0 ? "Server" : "Client " + participantId;
  }

}
