package domain;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.proxy.LoadBalancingProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;

import java.net.URI;

public class FrontendServer {
  private final Undertow server;

  // Assume default location in jar
  public FrontendServer(ClassLoader loader, String backendAddress, int port) {

    server = Undertow.builder()
        .addHttpListener(port, "0.0.0.0")
        .setHandler(createPathHandler(loader, backendAddress))
        .build();
  }

  private HttpHandler createPathHandler(ClassLoader loader, final String proxyTo) {
    ClassPathResourceManager resourceManager = new ClassPathResourceManager(loader, "client");
    ResourceHandler resourceHandler = Handlers.resource(
        resourceManager
    );
    return new PathHandler()
        .addPrefixPath("/umlp/api", ProxyHandler.builder()
            .setProxyClient(new LoadBalancingProxyClient().addHost(URI.create(proxyTo + "/umlp/api")))
            .build())
        .addPrefixPath("/", new HttpHandler() {
              @Override
              public void handleRequest(HttpServerExchange exchange) throws Exception {
                if(resourceManager.getResource(exchange.getRelativePath()) != null) {
                  resourceHandler.handleRequest(exchange);
                } else {
                  resourceManager.getResource("index.html").serve(
                      exchange.getResponseSender(),
                      exchange,
                      IoCallback.END_EXCHANGE
                  );
                }
              }
            }
        );
  }


  public void start() {
    server.start();
  }

  public void stop() {
    server.stop();
  }
}
