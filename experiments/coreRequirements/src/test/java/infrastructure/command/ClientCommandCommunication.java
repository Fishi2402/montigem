/* (c) https://github.com/MontiCore/monticore */
package infrastructure.command;

import umlp.command.Command;
import umlp.command.CommandResult;
import umlp.command.error.NotImplementedException;
import umlp.rte.Result;

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
