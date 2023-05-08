/* (c) https://github.com/MontiCore/monticore */
package infrastructure.command;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import stanley.FlensburgTestContext;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.service.websocket.ClientCommandWebsocketCommunication;

import java.util.concurrent.CountDownLatch;

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
public class TestClientWebSocketCommandCommunication
    extends ClientCommandWebsocketCommunication {
  private FlensburgTestContext serverContext;
  private FlensburgTestContext clientContext;
  private CountDownLatch externalLatch;

  public TestClientWebSocketCommandCommunication(String uri) {
    super(uri);
    onCommandReceived(command -> {
      serverContext.activate();
      // Möglichkeit zur Synchronisierung im Test
      countDownExternalLatch();
    });
    onCommandResultReceived(commandResult -> {
      serverContext.activate();
    });
  }

  @Override
  public synchronized <T> CommandResult<T> sendCommandToServerAndReceiveResult(Command<T> command) {
    serverContext.activate();
    CommandResult<T> result = super.sendCommandToServerAndReceiveResult(command);
    clientContext.activate();
    return result;
  }

  @Override
  protected synchronized void handleTextMessage(WebSocketSession session, TextMessage message) {
    if(!expectInitMessage()) {
      clientContext.activate();
      super.handleTextMessage(session, message);
    }
  }

  private void countDownExternalLatch() {
    if (externalLatch != null && externalLatch.getCount() > 0) {
      externalLatch.countDown();
      if (externalLatch.getCount() == 0) {
        externalLatch = null;
      }
    }
  }

  public CountDownLatch setCountDownLatchToIncomingMessages(int count) {
    this.externalLatch = new CountDownLatch(count);
    return this.externalLatch;
  }

  public void setContext(FlensburgTestContext serverContext,
      FlensburgTestContext clientContext) {
    this.serverContext = serverContext;
    this.clientContext = clientContext;
  }
}
