package domain.e2e;

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

public class UndertowFrontendHost {
  private final int port;
  private final File distDir;
  private final String proxyTo;

  public UndertowFrontendHost(File distDir, String target, int port) {
    this.port = port;
    this.distDir = distDir;
    this.proxyTo = target;
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
                      new IoCallback() {
                        @Override
                        public void onComplete(HttpServerExchange exchange, Sender sender) {

                        }

                        @Override
                        public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {

                        }
                      }
                  );
                }

              }
            }
        )
        ;
  }

  public void start() {
    Undertow server = Undertow.builder()
        .addHttpListener(port, "0.0.0.0")
        .setHandler(createPathHandler(distDir, proxyTo))
        .build();
    server.start();
  }

  public void stop() {

  }

  public static void main(String[] args) {
    UndertowFrontendHost host = new UndertowFrontendHost(new File("."), "http://httpbin.org/get", 8082);
    host.start();
  }
}
