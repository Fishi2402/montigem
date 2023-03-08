/* (c) https://github.com/MontiCore/monticore */
package infrastructure.command;

import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandResult;
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
}
