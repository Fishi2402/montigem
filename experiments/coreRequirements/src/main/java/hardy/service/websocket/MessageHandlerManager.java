package hardy.service.websocket;

import umlp.command.CommandObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

public class MessageHandlerManager {
  private static MessageHandlerManager INSTANCE;
  private final Map<String, List<IMessageHandler>> handlers = new HashMap<>();

  protected MessageHandlerManager() {
  }

  public static MessageHandlerManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new MessageHandlerManager();
    }

    return INSTANCE;
  }

  public Optional<String> handleIncomingMessage(String messageType, String msg,
      Optional<SessionContainer> sessionContainer) {
    final List<String> results = getInstance().handlers.entrySet().stream()
      .filter(e -> messageType.equals(MessageType.ALL.toString()) || e.getKey().equals(MessageType.ALL.toString()) || e.getKey().equals(messageType))
      .flatMap(e -> e.getValue().stream()).map(h -> h.handleIncomingMessage(messageType, msg, sessionContainer))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(Collectors.toList());
    if (results.isEmpty()) {
      return Optional.empty();
    }
    else {
//      return Optional.of(JsonMarshal.getDefaultSerializer().marshal(new MessageResult(results)));
      return Optional.of(results.stream().collect(Collectors.joining(",")));
    }
  }

  public MessageHandlerManager register(String messageType, IMessageHandler handler) {
    getInstance().handlers.putIfAbsent(messageType, new ArrayList<>());

    if (!getInstance().handlers.get(messageType).contains(handler)) {
      getInstance().handlers.get(messageType).add(handler);
    }
    return this;
  }

  public MessageHandlerManager unregister(String messageType, IMessageHandler handler) {
    if (getInstance().handlers.containsKey(messageType)) {
      getInstance().handlers.get(messageType).remove(handler);
    }
    return this;
  }
}
