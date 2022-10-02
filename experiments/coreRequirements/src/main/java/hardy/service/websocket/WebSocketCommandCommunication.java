/* (c) https://github.com/MontiCore/monticore */
package hardy.service.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import umlp.command.Command;
import umlp.command.CommandObjectMapper;
import umlp.command.ServerCommandCommunication;

import java.io.IOException;

public class WebSocketCommandCommunication implements ServerCommandCommunication {
  protected WebSocketSession session;

  public WebSocketCommandCommunication(WebSocketSession session) {
    this.session = session;
  }

  @Override
  public <T> void sendCommandToParticipant(Command<T> command) {
    try {
      session.sendMessage(new TextMessage(CommandObjectMapper.serializeCommand(command)));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
