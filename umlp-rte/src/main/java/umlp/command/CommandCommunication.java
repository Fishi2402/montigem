/* (c) https://github.com/MontiCore/monticore */
package umlp.command;

import umlp.command.error.NotImplementedException;

public interface CommandCommunication {
  default <T> void sendCommand(Command<T> command) {
    throw new NotImplementedException();
  }

  default <T> CommandResult<T> sendCommandAndReceiveResult(Command<T> command) {
    throw new NotImplementedException();
  }

  long init();
}
