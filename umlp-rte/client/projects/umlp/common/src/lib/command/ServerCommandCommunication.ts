/* (c) https://github.com/MontiCore/monticore */

import { Command } from "./Command";

/**
 * Simuliert die Kommunikation zwischen Server und einem Client
 */
export interface ServerCommandCommunication {

  /**
   * Sendet ein Command zu einem Client
   */
  sendCommandToParticipant<T>(command: Command<T>): void;
}

export class INTERNAL_ServerCommandCommunication {}
