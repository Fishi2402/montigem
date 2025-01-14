/* (c) https://github.com/MontiCore/monticore */
import {Command} from './Command';

/**
 * Simuliert die Kommunikation zwischen Server und einem Client
 * @class
 */
export interface ServerCommandCommunication {
  /**
   * Sendet ein Command zu einem Client
   * @param {Command} command
   */
  sendCommandToParticipant<T>(command: Command<T>);
}


