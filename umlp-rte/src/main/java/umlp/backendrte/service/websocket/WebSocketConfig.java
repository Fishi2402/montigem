/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.service.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
  private final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
  public final static String UMLP_DOMAINNAME_ENV = "UMLP_DEPLOYED_DOMAIN";

  @Autowired
  private WebSocketHandlerService webSocketHandlerService;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    String path = "/command";
    logger.info("Adding websocket handler at " + path);
    WebSocketHandlerRegistration handler = registry.addHandler(customWebSocketHandler(), path)
        .addInterceptors(new HttpSessionHandshakeInterceptor());

    if (System.getenv().containsKey(UMLP_DOMAINNAME_ENV)) {
      logger.info("Setting allowed origin " + UMLP_DOMAINNAME_ENV + " for websocket at  " + path);
      handler.setAllowedOrigins(System.getenv().get(UMLP_DOMAINNAME_ENV));
      // Doesn't work otherwise in deployed configuration (behind a proxy).
    }
  }

  @Bean
  public TextWebSocketHandler customWebSocketHandler() {
    return (TextWebSocketHandler) webSocketHandlerService;
  }

}
