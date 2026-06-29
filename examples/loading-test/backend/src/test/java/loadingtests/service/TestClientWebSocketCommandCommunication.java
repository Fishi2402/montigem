/* (c) https://github.com/MontiCore/monticore */
package loadingtests.service;

import loadingtests.LoadingTestsTestContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.service.websocket.ClientCommandWebsocketCommunication;

public class TestClientWebSocketCommandCommunication extends ClientCommandWebsocketCommunication {
  private LoadingTestsTestContext serverContext;
  private LoadingTestsTestContext clientContext;

  public TestClientWebSocketCommandCommunication(String uri) {
    super(uri, new WebSocketHttpHeaders());
    onCommandReceived(command -> {
      serverContext.activate();
    });
    onCommandResultReceived(commandResult -> {
      clientContext.activate();
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
//    if(!expectInitMessage()) {
      clientContext.activate();
      super.handleTextMessage(session, message);
//    }
  }

  public void setContext(LoadingTestsTestContext serverContext, LoadingTestsTestContext clientContext) {
    this.serverContext = serverContext;
    this.clientContext = clientContext;
  }
}
