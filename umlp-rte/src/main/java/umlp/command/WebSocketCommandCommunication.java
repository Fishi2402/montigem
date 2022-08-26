/* (c) https://github.com/MontiCore/monticore */
package umlp.command;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class WebSocketCommandCommunication implements CommandCommunication {
  protected WebSocketSession session;

  public WebSocketCommandCommunication(WebSocketSession session) {
    this.session = session;
  }

  @Override
  public void sendCommand(Command command) {
    try {
      session.sendMessage(new TextMessage(CommandObjectMapper.serializeCommand(command)));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
