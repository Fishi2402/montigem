/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.service.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.command.Participant;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.LoadResult;
import umlp.backendrte.common.LoadingState;

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
    System.out.println("Server received message");
    String returnResult = "";
    try {
      // TODO: check participant id is correct
      Optional<Command<Object>> commandOptional = CommandObjectMapper.deserializeCommand(message.getPayload());
      if (!commandOptional.isPresent()) {
        return;
      }
      Command command = commandOptional.get();
      CommandResult res = CommandManager.executeCommand(command);
      // Check if the object was found. If not, then someone might have tried to load a subclass
      // instance instead of a class instance
      // TODO AGe: isSuccessful should probably be a part of CommandResult interface
      // TODO AGe: all cases when such check is needed?
      if (res.isSimpleResult() && res.asSimpleResult().isSuccessful()
          || res.isListResult() && res.asListResult().isSuccessful()
          || res.isStatusResult() && res.asStatusResult().wasSuccessful()
      ) {
        returnResult = CommandObjectMapper.serializeResponse(res);
      }
      else {
        returnResult = CommandObjectMapper.serializeResponse(StatusResult.error(command.getId()));
      }
    } catch (Exception e) {
      returnResult = "Command could not be parsed!";
    }
    System.out.println(returnResult);
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
