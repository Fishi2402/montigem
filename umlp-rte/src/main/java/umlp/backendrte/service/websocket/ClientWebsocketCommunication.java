/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.service.websocket;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.MCFatalError;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.LoadingState;
import umlp.backendrte.common.Result;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ClientWebsocketCommunication extends TextWebSocketHandler {
  private long maxWaitingTime = 6000; // 6 seconds
  private long participantId = 0;
  private String uri;
  private WebSocketSession clientSession;
  private CountDownLatch initLatch;
  private Map<Long, CommandResult<?>> messages = new HashMap<>();

  public ClientWebsocketCommunication(String url, String port, String applicationName,
      String apiName) {
    this.uri = String.format("ws://%s:%s/%s/%s", url, port, applicationName, apiName);
  }

  public synchronized <T> CommandResult<T> sendCommandToServerAndReceiveResult(Command<T> command) {
    command.setParticipantId(participantId);
    CommandResult<T> result = null;
    try {
      String payload = CommandObjectMapper.serializeCommand(command);
      clientSession.sendMessage(new TextMessage(payload));
      synchronized (this) {
        this.wait(maxWaitingTime);
      }

      if (messages.containsKey(command.getId())) {
        CommandResult<?> response = messages.get(command.getId());
        messages.remove(command.getId());
        if (((ParameterizedType) command.getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0].getTypeName()
            .equals(response.getTypeParameter())) {
          // Stelle sicher, dass der Typ auch nach dem Deserialisieren gesichert ist
          result = (CommandResult<T>) response;
        }
        else {
          return new SimpleResult<>(command.getId(), LoadingState.FAILED_TO_LOAD, command.getCommandType());
        }
      } else {
        return new SimpleResult<>(command.getId(), LoadingState.FAILED_TO_LOAD, command.getCommandType());
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      return new SimpleResult<>(command.getId(), LoadingState.FAILED_TO_LOAD, command.getCommandType());
    }
    catch (InterruptedException e) {
      e.printStackTrace();
      return new SimpleResult<>(command.getId(), LoadingState.FAILED_TO_LOAD, command.getCommandType());
    }
    return result;
  }

  public Result<Long> init() {
    Result<Long> result = Result.error();
    initLatch = new CountDownLatch(2);
    StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
    try {
      this.clientSession = webSocketClient.doHandshake(this, new WebSocketHttpHeaders(),
          URI.create(uri)).get();
    }
    catch (InterruptedException e) {
      Log.warn(String.format("Could not establish connection to URI '%s'", uri));
      return Result.error();
    }
    catch (ExecutionException e) {
      Log.warn(String.format("Could not establish connection to URI '%s'", uri));
      return Result.error();
    }

    try {
      // Warte bis die Verbindung aufgebaut ist und eine Antwort eintrifft
      boolean await = initLatch.await(5, TimeUnit.SECONDS);
      if (await) {
        result = Result.ok(this.participantId);
      }
      else {
        return Result.error();
      }
    }
    catch (InterruptedException e) {
      e.printStackTrace();
      return Result.error();
    }

    return result;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    initLatch.countDown();
  }

  @Override
  protected synchronized void handleTextMessage(WebSocketSession session, TextMessage message) {
    if (initLatch.getCount() > 0) {
      // Fall initialisierung der Verbindung
      this.participantId = Integer.parseInt(message.getPayload());
      initLatch.countDown();
    }
    else {
      // Fall Antwort eines Requests, oder Erhalt eines weitergeleiteten Commands zur
      // Datensynchronisierung.
      // TODO AHe, MH: Wasteful way of distinguishing commands and results
      Optional<CommandResult<?>> commandResultOptional = Optional.empty();
      try {
        commandResultOptional = CommandObjectMapper.deserializeResponse(message.getPayload());
      } catch (MCFatalError ex) {

      }
      Optional<Command<Object>> commandOptional = Optional.empty();
      try {
        commandOptional = CommandObjectMapper.deserializeCommand(message.getPayload());
      } catch (MCFatalError ex) {

      }

      if (commandResultOptional.isPresent()) { // TODO MH: Need to check for participantId
        // Fall Antwort eines Requests
        CommandResult<?> commandResult = commandResultOptional.get();
        messages.put(commandResult.getCommandId(), commandResult);
        synchronized (this) {
          notify();
        }
      }
      else if (commandOptional.isPresent()) {
        //Fall Antwort eines Erhalt eines weitergeleiteten Commands zur Datensynchronisierung
        Log.info("Received sync command from server: " + CommandObjectMapper.serializeCommand(commandOptional.get()), "UMLP ClientWebsocket");
//        commandOptional.get().doRun();
      }
      else {
        synchronized (this) {
          notify();
        }
      }
    }
  }

  public boolean isOpen() {
    return clientSession.isOpen();
  }
}
