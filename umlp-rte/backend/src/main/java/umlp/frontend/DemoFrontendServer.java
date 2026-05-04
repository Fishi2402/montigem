package umlp.frontend;

import de.se_rwth.commons.logging.Log;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.proxy.ProxyHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.proxy.LoadBalancingProxyClient;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.URI;

@Component
@ConditionalOnProperty(
    name = "java.frontend.enabled",
    havingValue = "true",
    matchIfMissing = false
)
public class DemoFrontendServer {

  private final int port;

  @Value("${server.port:8081}")
  private int backendPort = 8081;
  // Removed File distDir as it is no longer needed
  private final String backendAddress;

  @Value("${server.servlet.context-path:/umlp/api}")
  private String backendProxyPath="/umlp/api";

  private final Undertow server;

  // specific resource path inside src/main/resources
  private static final String RESOURCE_PATH = "browser";

  public DemoFrontendServer() {
    this.port = 4200;
    this.backendAddress = "http://localhost:"+backendPort;
    // Removed distDir from arguments
    this.server = Undertow.builder()
        .addHttpListener(port, "0.0.0.0")
        .setHandler(this.createPathHandler(backendAddress))
        .build();
  }


  @PostConstruct
  public void start() {
    Log.info("java.frontend.enabled=true. Starting FrontEnd Server to locally host " +
        "web-sources from jar on port "+port, this.getClass().getName());
    this.server.start();
  }

  @PreDestroy
  public void stop() {
    this.server.stop();
  }

  private HttpHandler createPathHandler(String proxyTo) {
    // 1. Change to ClassPathResourceManager
    // This looks for files inside the JAR/Classpath under the "website" folder
    final ResourceManager resourceManager = new ClassPathResourceManager(
        DemoFrontendServer.class.getClassLoader(),
        RESOURCE_PATH
    );

    final ResourceHandler resourceHandler = Handlers.resource(resourceManager);

    URI proxyHost = URI.create(proxyTo + backendProxyPath);
    System.out.println("Proxying to " + proxyHost);
    return (new PathHandler())
        .addPrefixPath(backendProxyPath, ProxyHandler.builder()
            .setProxyClient(new LoadBalancingProxyClient().addHost(proxyHost))
            .build())
        .addPrefixPath("/", new HttpHandler() {
          public void handleRequest(HttpServerExchange exchange) throws Exception {
            // 2. Logic remains the same: Check if resource exists, else serve index.html
            // Note: getResource returns null if the file isn't found in the classpath
            if (resourceManager.getResource(exchange.getRelativePath()) != null) {
              resourceHandler.handleRequest(exchange);
            } else {
              // Serves index.html for SPA routing (Angular/React/Vue) handling
              resourceManager.getResource("index.html")
                  .serve(exchange.getResponseSender(), exchange, IoCallback.END_EXCHANGE);
            }
          }
        });
  }

}
