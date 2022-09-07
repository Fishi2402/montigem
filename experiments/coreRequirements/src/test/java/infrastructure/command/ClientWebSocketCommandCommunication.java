/* (c) https://github.com/MontiCore/monticore */
package infrastructure.command;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import stanley.FlensburgTestContext;
import umlp.command.*;
import umlp.rte.Result;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * Simuliert die Kommunikation zwischen Client und Server durch Websockets. Hier werden
 * verschiedene Threads genutzt, was die Synchronisation erschwert. Die Initialisierung und
 * das Versenden eines Kommandos und dessen Warten auf eine Antwort sind bereits synchron
 * implementiert. Allerdings muss darauf geachtet werden, dass andere Clients ihre
 * (weitergeleiteten) Commands zur Datensynchronisierung erhalten, bevor das eigene Command
 * abgearbeitet ist. Das kann durch den Mechanismus der Latches erreicht werden.
 *
 * Auch hier muss erst der ServerContext hergestellt werden und später wieder der ClientContext.
 */
public class ClientWebSocketCommandCommunication extends TextWebSocketHandler
    implements ClientCommandCommunication {
  private long participantId = 0;
  private FlensburgTestContext serverContext;
  private FlensburgTestContext clientContext;

  private WebSocketSession clientSession;
  private String uri;
  private int port;
  private long maxWaitingTime = 60000;

  private CountDownLatch initLatch;
  private CountDownLatch externalLatch;

  private Map<Long, CommandResult<?>> messages = new HashMap<>();

  public ClientWebSocketCommandCommunication(String uri, int port) {
    this.uri = uri;
    this.port = port;
  }

  @Override
  public synchronized <T> CommandResult<T> sendCommandToServerAndReceiveResult(Command<T> command) {
    serverContext.activate();
    CommandResult<T> result = null;

    try {
      String payload = CommandObjectMapper.serializeCommand(command);
      clientSession.sendMessage(new TextMessage(payload));
      synchronized (this) {
        // Warte bis eine Antwort eintrifft
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
          // TODO MH
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      // TODO MH
    }
    catch (InterruptedException e) {
      e.printStackTrace();
      // TODO MH
    }

    clientContext.activate();
    return result;
  }

  @Override
  public Result<Long> init() {
    Result<Long> result = Result.error();
    initLatch = new CountDownLatch(2);
    StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
    try {
      this.clientSession = webSocketClient.doHandshake(this, new WebSocketHttpHeaders(),
          URI.create(String.format(uri, port))).get();
    }
    catch (InterruptedException e) {
      // TODO MH
    }
    catch (ExecutionException e) {
      // TODO MH
    }

    try {
      // Warte bis die Verbindung aufgebaut ist und eine Antwort eintrifft
      boolean await = initLatch.await(5, TimeUnit.SECONDS);
      if (await) {
        result = Result.ok(this.participantId);
      }
      else {
        // TODO MH
      }
    }
    catch (InterruptedException e) {
      // TODO MH
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
      clientContext.activate();
      Optional<CommandResult<?>> commandResultOptional = CommandObjectMapper.deserializeResponse(
          message.getPayload());
      Optional<Command<?>> commandOptional = CommandObjectMapper.deserializeCommand(
          message.getPayload());

      if (commandResultOptional.isPresent()) { // TODO MH: Need to check for participantId
        // Fall Antwort eines Requests
        CommandResult<?> commandResult = commandResultOptional.get();
        messages.put(commandResult.getCommandId(), commandResult);
        synchronized (this) {
          notify();
        }
        serverContext.activate();
      }
      else if (commandOptional.isPresent()) {
        //Fall Antwort eines Erhalt eines weitergeleiteten Commands zur Datensynchronisierung
        commandOptional.get().doRun();
        serverContext.activate();
        // Möglichkeit zur Synchronisierung im Test
        countDownExternalLatch();
      }
      else {
        // TODO MH
      }
    }
  }

  public void setContext(FlensburgTestContext serverContext,
      FlensburgTestContext clientContext) {
    this.serverContext = serverContext;
    this.clientContext = clientContext;
  }

  public CountDownLatch setCountDownLatchToIncomingMessages(int count) {
    this.externalLatch = new CountDownLatch(count);
    return this.externalLatch;
  }

  private void countDownExternalLatch() {
    if (externalLatch != null && externalLatch.getCount() > 0) {
      externalLatch.countDown();
      if (externalLatch.getCount() == 0) {
        externalLatch = null;
      }
    }
  }
}
