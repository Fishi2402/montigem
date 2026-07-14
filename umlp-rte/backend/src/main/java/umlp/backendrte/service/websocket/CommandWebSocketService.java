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
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.command.Participant;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.util.LogUtil;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandWebSocketService extends TextWebSocketHandler implements WebSocketHandlerService {

  protected Map<String, Long> clientIds = new ConcurrentHashMap<>();
  protected Map<WebSocketSession, Participant> participants = new ConcurrentHashMap<>();
  private final Map<WebSocketSession, StringBuilder> messageBuffers = new ConcurrentHashMap<>();

  protected String getSessionPrincipalName(WebSocketSession session) {
    return session.getPrincipal().getName();
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    Long subscriberId;
    if (clientIds.containsKey(session.getId())) {
      subscriberId = clientIds.get(session.getId());
    } else {
      WebSocketCommandCommunication commandCommunication = new WebSocketCommandCommunication(session);
      Participant participant = new Participant(commandCommunication, getSessionPrincipalName(session));
      participants.put(session, participant);

      subscriberId = CommandManager.register(participant);
      clientIds.put(session.getId(), subscriberId);
    }
    synchronized (session) {
      session.sendMessage(new TextMessage(Long.toString(subscriberId)));
    }
  }

  @Override
  public boolean supportsPartialMessages() {
    return true;
  }

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
    StringBuilder buffer = messageBuffers.computeIfAbsent(session, k -> new StringBuilder());
    buffer.append(message.getPayload());

    if (!message.isLast()) {
      return; // Wait for the final part
    }

    String payload = buffer.toString();
    messageBuffers.remove(session); // Remove from map to allow Garbage Collection of large buffers

    Log.info("Received:\t" + LogUtil.concealSensitives(payload), this.getClass().getName());
    String returnResult = "";
    try {
      Optional<Command<Object>> commandOptional = CommandObjectMapper.deserializeCommand(payload);
      if (commandOptional.isEmpty()) {
        returnResult = handleUnknownError(payload);
      } else {
        Command<?> command = commandOptional.get();

        // check that participant id is correct
        var participantFromSession = Optional.ofNullable(participants.get(session));
        if (participantFromSession.isEmpty()) {
          throw new IllegalStateException("User is not logged in!");
        }

        var participantFromId = Optional.ofNullable(
            CommandManager.getInstance().getParticipant(command.getParticipantId()));

        if (!participantFromSession.equals(participantFromId)) {
          Log.warn(
              "Participant from id " + command.getId() + ":" + participantFromId + "was not the same as participant from session " + participantFromSession);
          command.setParticipantId(participantFromSession.get().getParticipantId());
        }

        CommandResult<?> res = CommandManager.executeCommand(command);
        returnResult = CommandObjectMapper.serializeResponse(res);
      }
    } catch (Exception e) {
      Log.error("Command could not be parsed:\n"+ payload, e);
      returnResult = handleUnknownError(payload);
    }
    Log.info("Responding: " + LogUtil.concealSensitives(returnResult), this.getClass().getName());
    TextMessage msg = new TextMessage(returnResult);
    synchronized (session) {
      Log.info("Session "+ session.toString()+ " client "+participants.get(session), this.getClass().getName());
      session.sendMessage(msg);
    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    Log.warn("Encountered error in WebSocket Connection with client "+participants.get(session).getParticipantId(), exception);
    super.handleTransportError(session, exception);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    Log.info("Closing websocket connection for client "+participants.get(session).getParticipantId()+
        ". CloseStatus: "+status.toString(), this.getClass().getName());
    messageBuffers.remove(session);
    if (clientIds.containsKey(session.getId())) {
      CommandManager.deregister(clientIds.get(session.getId()));
      clientIds.remove(session.getId());
    }
  }

  public Map<String, Long> getClientIds() {
    return clientIds;
  }

  protected String handleUnknownError(String payload) {
    int commandId = -1;
    Pattern p = Pattern.compile("\"id\":\\s*(\\d+)");
    Matcher m = p.matcher(payload);
    if (m.find()) {
      commandId = Integer.parseInt(m.group(1));
    }
    return CommandObjectMapper.serializeResponse(StatusResult.error(commandId));
  }
}
