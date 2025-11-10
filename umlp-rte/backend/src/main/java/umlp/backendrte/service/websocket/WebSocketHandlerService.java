/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.service.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

public interface WebSocketHandlerService extends WebSocketHandler {
  void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception;
}


