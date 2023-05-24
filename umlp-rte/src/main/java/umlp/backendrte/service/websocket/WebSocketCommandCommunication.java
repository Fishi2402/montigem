/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.service.websocket;

import de.se_rwth.commons.logging.Log;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.command.ServerCommandCommunication;

import java.io.IOException;

public class WebSocketCommandCommunication implements ServerCommandCommunication {
  protected WebSocketSession session;

  public WebSocketCommandCommunication(WebSocketSession session) {
    this.session = new ConcurrentWebSocketSessionDecorator(session, 1000, 1024);
//    this.session = session;
  }

  @Override
  public <T> void sendCommandToParticipant(Command<T> command) {
    Log.trace(String.format("Sending command %s to session %s", command.getCommandType(), session.getId()), "WebSocketCommandCommunication");
    boolean sendingSuccessful = WebSocketSessionManager.sendMessage(session, CommandObjectMapper.serializeCommand(command));
    if (!sendingSuccessful) {
      Log.error(String.format("0xWS002 Could not send command %s to the server", command.getCommandType()));
    }
  }
}
