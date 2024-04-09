/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.service.websocket;

import de.se_rwth.commons.logging.Log;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.command.Participant;
import umlp.backendrte.command.StatusResult;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandWebSocketNoAuthService extends CommandWebSocketService {

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    Long subscriberId;
    if (clientIds.containsKey(session.getId())) {
      subscriberId = clientIds.get(session.getId());
    }
    else {
      WebSocketCommandCommunication commandCommunication = new WebSocketCommandCommunication(session);
      Participant participant = new Participant(commandCommunication);
      participants.put(session, participant);

      subscriberId = CommandManager.register(participant);
      clientIds.put(session.getId(), subscriberId);
    }
    synchronized (session) {
      session.sendMessage(new TextMessage(Long.toString(subscriberId)));
    }
  }

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
    String payload = message.getPayload();
    System.out.println("Received:\t" + payload);
    String returnResult = "";
    try {
      Optional<Command<Object>> commandOptional = CommandObjectMapper.deserializeCommand(payload);
      if (commandOptional.isEmpty()) {
        return;
      }
      Command command = commandOptional.get();

      // check that participant id is correct
      var participantFromSession = Optional.ofNullable(participants.get(session));
      if (participantFromSession.isEmpty()) {
        throw new IllegalStateException("User is not logged in!");
      }

      CommandResult res = CommandManager.executeCommand(command);
      returnResult = CommandObjectMapper.serializeResponse(res);
    }
    catch (Exception e) {
      int commandId = -1;
      Pattern p = Pattern.compile("\"id\":\\s*(\\d+)");
      Matcher m = p.matcher(payload);
      if (m.find()) {
        commandId = Integer.parseInt(m.group(1));
      }
      Log.error("Command could not be parsed:\n" + payload, e);
      returnResult = CommandObjectMapper.serializeResponse(StatusResult.error(commandId));
    }
    System.out.println("Responding: " + returnResult);
    System.out.println(session);
    synchronized (session) {
      session.sendMessage(new TextMessage(returnResult));
    }
  }

}
