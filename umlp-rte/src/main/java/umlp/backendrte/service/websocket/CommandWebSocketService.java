/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.service.websocket;

import de.se_rwth.commons.logging.Log;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.command.Participant;
import umlp.backendrte.command.StatusResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandWebSocketService extends TextWebSocketHandler {

  public static final String LOG_NAME = "UMLP CommandWebsocketService";

  Map<String, Long> clientIds = new HashMap<>();
  Map<WebSocketSession, Participant> participants = new HashMap<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    ConcurrentWebSocketSessionDecorator synchronizedSession = WebSocketSessionManager.register(
        session);
    Long subscriberId;
    if (clientIds.containsKey(session.getId())) {
      subscriberId = clientIds.get(session.getId());
      Log.info("Re-establishing connection with client " + subscriberId, LOG_NAME);
      Log.trace("Re-establishing connection with client " + subscriberId + " and session " + session.getId(), LOG_NAME);
    } else {
      WebSocketCommandCommunication commandCommunication = new WebSocketCommandCommunication(synchronizedSession);
      Participant participant = new Participant(commandCommunication);
      participants.put(session, participant);

      subscriberId = CommandManager.register(participant);
      clientIds.put(session.getId(), subscriberId);
      Log.info("Establishing connection with client " + subscriberId, LOG_NAME);
      Log.trace("Establishing connection with client " + subscriberId + " and session " + session.getId(), LOG_NAME);
    }
    WebSocketSessionManager.sendMessage(session, Long.toString(subscriberId));
  }

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
    Log.info("Server received message: " + message, LOG_NAME);
    Log.trace("Server received message from session " + session.getId(), LOG_NAME);
    String returnResult = "";
    try {
      // TODO: check participant id is correct
      Optional<Command<Object>> commandOptional = CommandObjectMapper.deserializeCommand(message.getPayload());
      if (!commandOptional.isPresent()) {
        returnResult = CommandObjectMapper.serializeResponse(StatusResult.error(-1));
      } else {
        CommandResult<Object> result = CommandManager.executeCommand(commandOptional.get());
        returnResult = CommandObjectMapper.serializeResponse(result);
      }
    } catch (Exception e) {
      returnResult = "An error occured while executing the command. Check your syntax";
    }
    Log.info(String.format("Return message %s over websocket to client %s",  clientIds.get(session.getId()), returnResult), LOG_NAME);
    Log.trace(String.format("Return message %s over websocket to client %s with session %s", returnResult, clientIds.get(session.getId()), session.getId()), LOG_NAME);
    WebSocketSessionManager.sendMessage(session, returnResult);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    if (clientIds.containsKey(session.getId())) {
      Log.info(String.format("Closing connection for client %s and session %s with status %s", clientIds.get(session.getId()), session.getId(), status.toString()), LOG_NAME);
      CommandManager.deregister(clientIds.get(session.getId()));
      clientIds.remove(session.getId());
    } else {
      Log.info(String.format("Closing connection for client session %s with status %s", session.getId(), status.toString()), LOG_NAME);
    }
    WebSocketSessionManager.deregister(session);
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) {
    Log.warn(String.format("Websocket transportation error: %s", exception.getMessage()));
  }
}
