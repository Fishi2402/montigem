/* (c) https://github.com/MontiCore/monticore */

package hardy.service.websocket;

import java.util.Optional;

@FunctionalInterface
public interface IMessageHandler {
  Optional<String> handleIncomingMessage(String messageType, String msg, Optional<SessionContainer> sessionContainer);
}
