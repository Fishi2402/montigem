/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.service.websocket;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.MCFatalError;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import umlp.backendrte.command.ClientCommandCommunication;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.command.CommandReceiveObserver;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.command.CommandResultReceiveObserver;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.LoadingState;
import umlp.backendrte.common.Result;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ClientCommandWebsocketCommunication extends TextWebSocketHandler implements
    ClientCommandCommunication {
  private long maxWaitingTime = 6000; // 6 seconds
  private Optional<Long> participantId = Optional.empty();
  private WebSocketSession session;
  private CountDownLatch initLatch;
  private Map<Long, CommandResult<?>> messages = new HashMap<>();

  private List<CommandReceiveObserver> commandObserver = new ArrayList<>();
  private List<CommandResultReceiveObserver> commandResultObserver = new ArrayList<>();

  private final String uri;

  private final WebSocketHttpHeaders headers;

  public ClientCommandWebsocketCommunication(String uri) {
    this.uri = uri;
    this.headers = new WebSocketHttpHeaders();
  }

  public ClientCommandWebsocketCommunication(String uri, WebSocketHttpHeaders headers) {
    this.uri = uri;
    this.headers = headers;
  }

  public synchronized <T> CommandResult<T> sendCommandToServerAndReceiveResult(Command<T> command) {
    command.setParticipantId(participantId.get());
    CommandResult<T> result;
    try {
      String payload = CommandObjectMapper.serializeCommand(command);
      session.sendMessage(new TextMessage(payload));
      synchronized (this) {
        this.wait(maxWaitingTime);
      }

      if (messages.containsKey(command.getId())) {
        CommandResult<?> response = messages.get(command.getId());
        messages.remove(command.getId());
        // Stelle sicher, dass der Typ auch nach dem Deserialisieren gesichert ist
        if (((ParameterizedType) command.getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0].getTypeName()
            .equals(response.getTypeParameter())) {
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
    initLatch = new CountDownLatch(2);
    StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
    try {
      this.session = webSocketClient.doHandshake(this, headers,
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
      boolean await = initLatch.await(2, TimeUnit.SECONDS);
      if (await && this.participantId.isPresent()) {
        return Result.ok(this.participantId.get());
      }
      else {
        return Result.error();
      }
    }
    catch (InterruptedException e) {
      e.printStackTrace();
      return Result.error();
    }
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    initLatch.countDown();
  }

  @Override
  protected synchronized void handleTextMessage(WebSocketSession session, TextMessage message) {
    if (expectInitMessage()) {
      // Fall initialisierung der Verbindung
      try {
        this.participantId = Optional.of(Long.parseLong(message.getPayload()));
      }
      catch (NumberFormatException e) {
        this.participantId = Optional.empty();
      }
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
        for (CommandResultReceiveObserver observer : commandResultObserver) {
          observer.accept(commandResult);
        }
        synchronized (this) {
          notify();
        }
      }
      else if (commandOptional.isPresent()) {
        //Fall Antwort eines Erhalt eines weitergeleiteten Commands zur Datensynchronisierung
        for (Consumer<Command<Object>> consumer : commandObserver) {
          consumer.accept(commandOptional.get());
        }
      }
      else {
        synchronized (this) {
          notify();
        }
      }
    }
  }

  protected boolean expectInitMessage() {
    return initLatch.getCount() > 0;
  }

  public ClientCommandWebsocketCommunication onCommandReceived(CommandReceiveObserver observer) {
    this.commandObserver.add(observer);
    return this;
  }

  public ClientCommandWebsocketCommunication onCommandResultReceived(CommandResultReceiveObserver observer) {
    this.commandResultObserver.add(observer);
    return this;
  }

  public boolean isOpen() {
    return session.isOpen();
  }
}
