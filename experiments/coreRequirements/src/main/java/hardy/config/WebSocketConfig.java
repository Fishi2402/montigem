/* (c) https://github.com/MontiCore/monticore */
package hardy.config;

import hardy.service.websocket.CommandWebSocketService;
import org.apache.catalina.Context;
import org.apache.tomcat.websocket.server.WsSci;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
  final static String UMLP_DOMAINNAME_ENV = "UMLP_DEPLOYED_DOMAIN";

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    WebSocketHandlerRegistration handler= registry.addHandler(new CommandWebSocketService(), "/command")
        .addInterceptors(new HttpSessionHandshakeInterceptor());

    if(System.getenv().containsKey(UMLP_DOMAINNAME_ENV)){
      handler.setAllowedOrigins(System.getenv().get(UMLP_DOMAINNAME_ENV));
          // Doesn't work otherwise in deployed configuration (behind a proxy).
    }
  }

  @Bean
  public TomcatContextCustomizer tomcatContextCustomizer() {
    return new TomcatContextCustomizer() {
      @Override
      public void customize(Context context) {
        context.addServletContainerInitializer(new WsSci(), null);
      }
    };
  }
}
