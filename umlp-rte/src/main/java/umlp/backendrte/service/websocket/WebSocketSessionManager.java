/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.service.websocket;

import de.se_rwth.commons.logging.Log;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WebSocketSessionManager {

  public static final int MAX_TRIES = 10;

  private static WebSocketSessionManager INSTANCE;

  // Collection of all connected clients
  private final Map<String, ConcurrentWebSocketSessionDecorator> sessionList;

  public WebSocketSessionManager() {
    sessionList = new HashMap<>();
  }

  public static WebSocketSessionManager getINSTANCE() {
    if (INSTANCE == null) {
      INSTANCE = new WebSocketSessionManager();
    }
    return INSTANCE;
  }

  public static ConcurrentWebSocketSessionDecorator register(WebSocketSession session) {
    ConcurrentWebSocketSessionDecorator s = new ConcurrentWebSocketSessionDecorator(session,
        1000, 1024);
    getINSTANCE().sessionList.putIfAbsent(session.getId(), s);
    Log.trace("New websocket session (" + session.getId() + ") has been added", getINSTANCE().getClass().getName());
    return s;
  }

  public static void deregister(WebSocketSession session) {
    getINSTANCE().sessionList.remove(session.getId());
    Log.trace("Websocket session (" + session.getId() + ") has been removed", getINSTANCE().getClass().getName());
  }

  public static Optional<ConcurrentWebSocketSessionDecorator> findSession(WebSocketSession session) {
    return Optional.ofNullable(getINSTANCE().sessionList.getOrDefault(session.getId(), null));
  }

  /*************** sending ***************/
  public static boolean sendMessage(WebSocketSession session, String message) {
    Exception lastException = null;
    int tries = 0;
    Optional<ConcurrentWebSocketSessionDecorator> synchronizedSession = findSession(session);
    if (!synchronizedSession.isPresent()) {
      Log.error("0xWS003 An error occured on sending a websocket message", lastException);
      return false;
    }

    while (tries < MAX_TRIES) {
      try {
        synchronizedSession.get().sendMessage(new TextMessage(message));
        return true;
      } catch (IllegalStateException e) {
        lastException = e;
        tries++;
      }
      catch (IOException e) {
        lastException = e;
        tries++;
      }
    }

    Log.error("0xWS001 An error occured on sending a websocket message", lastException);
    return false;
  }
}
