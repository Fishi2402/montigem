/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.command;

import umlp.backendrte.command.error.NotImplementedException;

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
