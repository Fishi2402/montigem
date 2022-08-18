/* (c) https://github.com/MontiCore/monticore */
package hardy.config;

import hardy.service.websocket.CommandWebSocketService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(new CommandWebSocketService(), "/command")
        .addInterceptors(new HttpSessionHandshakeInterceptor());
  }

}
