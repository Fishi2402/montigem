/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.command;

import umlp.backendrte.common.Result;

/**
 * Simuliert die Kommunikation zwischen Client und Server
 */
public interface ClientCommandCommunication {

  /**
   * Sendet ein Command an den Server und wartet auf das Ergebnis
   */
  <T> CommandResult<T> sendCommandToServerAndReceiveResult(Command<T> command);

  /**
   * Initiiert die Verbindung und liefert die vom Server verteilte Participant Id
   */
  Result<Long> init();

  public default ClientCommandCommunication onCommandReceived(CommandReceiveObserver observer) {
    return this;
  }

  public default ClientCommandCommunication onCommandResultReceived(CommandResultReceiveObserver observer) {
    return this;
  }
}
