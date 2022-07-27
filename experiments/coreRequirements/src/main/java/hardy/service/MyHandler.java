/* (c) https://github.com/MontiCore/monticore */
package hardy.service;

import hardy.FlensburgManager;
import hardy.commands.Person_setNickname;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import umlp.command.Command;
import umlp.command.CommandObjectMapper;
import umlp.command.CommandResult;

import java.io.IOException;
import java.util.stream.Collectors;

public class MyHandler extends TextWebSocketHandler {
  @Override public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    super.afterConnectionEstablished(session);
  }

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
    String collect = FlensburgManager.getPersonList().stream().map(p -> "" + p.getGemId())
        .collect(Collectors.joining(", "));
    System.out.println(collect);

    String returnResult = "";
    try {
      Command command = CommandObjectMapper.deserializeCommand(message.getPayload());
      CommandResult commandResult = command.doRun();
      returnResult = CommandObjectMapper.serializeResponse(commandResult);
    } catch (Exception e) {
      returnResult = "Command could not be parsed!";
    }
    System.out.println(returnResult);
    session.sendMessage(new TextMessage(returnResult));
  }

}
