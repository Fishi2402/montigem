/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.service.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
  final static String UMLP_DOMAINNAME_ENV = "UMLP_DEPLOYED_DOMAIN";

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    WebSocketHandlerRegistration handler = registry.addHandler(new CommandWebSocketService(), "/command")
        .addInterceptors(new HttpSessionHandshakeInterceptor());

    if (System.getenv().containsKey(UMLP_DOMAINNAME_ENV)) {
      handler.setAllowedOrigins(System.getenv().get(UMLP_DOMAINNAME_ENV));
      // Doesn't work otherwise in deployed configuration (behind a proxy).
    }
  }
}
