/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.service.websocket;

import de.se_rwth.commons.logging.Log;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.command.Participant;
import umlp.backendrte.command.StatusResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandWebSocketService extends TextWebSocketHandler {

  Map<String, Long> clientIds = new HashMap<>();
  Map<WebSocketSession, Participant> participants = new HashMap<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    Long subscriberId;
    if (clientIds.containsKey(session.getId())) {
      subscriberId = clientIds.get(session.getId());
    } else {
      WebSocketCommandCommunication commandCommunication = new WebSocketCommandCommunication(session);
      Participant participant = new Participant(commandCommunication);
      participants.put(session, participant);

      subscriberId = CommandManager.register(participant);
      clientIds.put(session.getId(), subscriberId);
    }
    session.sendMessage(new TextMessage(Long.toString(subscriberId)));
  }

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
    Log.info("Server received message: " + message, "UMLP CommandWebsocketService");
    String returnResult = "";
    try {
      // TODO: check participant id is correct
      Optional<Command<Object>> commandOptional = CommandObjectMapper.deserializeCommand(message.getPayload());
      if (!commandOptional.isPresent()) {
        returnResult = CommandObjectMapper.serializeResponse(StatusResult.error(-1));
      } else {
        returnResult = CommandObjectMapper
            .serializeResponse(CommandManager.executeCommand(commandOptional.get()));
      }
    } catch (Exception e) {
      returnResult = "An error occured while executing the command. Check your syntax";
    }
    Log.info(returnResult, "UMLP CommandWebsocketService");
    session.sendMessage(new TextMessage(returnResult));
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    if (clientIds.containsKey(session.getId())) {
      CommandManager.deregister(clientIds.get(session.getId()));
      clientIds.remove(session.getId());
    }
  }
}
