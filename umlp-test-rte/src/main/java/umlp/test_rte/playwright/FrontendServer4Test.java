package umlp.test_rte.playwright;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.proxy.LoadBalancingProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class FrontendServer4Test {
  private final int port;
  private final File distDir;
  private final String backendAddress;
  private final Undertow server;

  public FrontendServer4Test(File distDir, String backendAddress, int port) {
    this.port = port;
    this.distDir = distDir;
    this.backendAddress = backendAddress;

    server = Undertow.builder()
        .addHttpListener(port, "0.0.0.0")
        .setHandler(createPathHandler(distDir, backendAddress))
        .build();
  }

  private HttpHandler createPathHandler(final File distDir, final String proxyTo) {
    PathResourceManager resourceManager = new PathResourceManager(distDir.toPath());
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
        )
        ;
  }

  public void start() {
    server.start();
  }

  public void stop() {
    server.stop();
  }
}
