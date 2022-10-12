/* (c) https://github.com/MontiCore/monticore */
package hardy.service.websocket;

import hardy.commands.Person_setAge;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import umlp.command.*;

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
    String returnResult = "";
    try {
      // TODO: check participant id is correct
      Optional<Command<?>> commandOptional = CommandObjectMapper.deserializeCommand(message.getPayload());
      if (!commandOptional.isPresent()) {
        return;
      }
      Command command = commandOptional.get();
      CommandResult res = CommandManager.executeCommand(command);
      returnResult = CommandObjectMapper.serializeResponse(res);
    } catch (Exception e) {
      returnResult = "Command could not be parsed!";
      returnResult += "\n Example for a command: " +
          CommandObjectMapper.serializeCommand(new Person_setAge(0, 5));
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
