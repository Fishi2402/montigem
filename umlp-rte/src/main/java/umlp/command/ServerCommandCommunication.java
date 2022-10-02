/* (c) https://github.com/MontiCore/monticore */
package umlp.command;

import umlp.command.error.NotImplementedException;

/**
 * Simuliert die Kommunikation zwischen Server und einem Client
 */
public interface ServerCommandCommunication {

  /**
   * Sendet ein Command zu einem Client
   */
  default <T> void sendCommandToParticipant(Command<T> command) {
    throw new NotImplementedException();
  }

}
