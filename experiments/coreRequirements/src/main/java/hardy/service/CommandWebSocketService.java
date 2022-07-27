/* (c) https://github.com/MontiCore/monticore */
package hardy.service;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class CommandWebSocketService {

  @MessageMapping("/hello")
  @SendTo("/topic/messages")
  public String greeting(String message) throws Exception {
    Thread.sleep(1000); // simulated delay
    return "Hello, " + HtmlUtils.htmlEscape(message) + "!";
  }
}
