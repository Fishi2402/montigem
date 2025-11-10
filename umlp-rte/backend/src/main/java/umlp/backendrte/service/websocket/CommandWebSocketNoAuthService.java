/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.service.websocket;

import org.springframework.web.socket.WebSocketSession;

public class CommandWebSocketNoAuthService extends CommandWebSocketService {

  @Override
  protected String getSessionPrincipalName(WebSocketSession session) {
    return "";
  }

}
