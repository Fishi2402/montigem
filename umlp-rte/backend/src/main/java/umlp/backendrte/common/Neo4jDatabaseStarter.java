package umlp.backendrte.common;

import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.harness.Neo4jBuilders;
import org.neo4j.harness.Neo4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Can be used to start a Neo4j instance from this program.
 * Should only be used for short-lived instances, like tests.
 * Permanent deployments should probably use a neo4j container
 */
public class Neo4jDatabaseStarter {
  protected static int port = -1;

  protected Neo4j embeddedDatabaseServer = null;

  public Neo4j getEmbeddedDatabaseServer() {
    return this.embeddedDatabaseServer;
  }

  public int start(Function<Integer, Path> portToDBBasePath) throws IOException {
    if(this.embeddedDatabaseServer == null) {
      port = getFreePort();
      this.embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
          .withDisabledServer()
          .withWorkingDir(portToDBBasePath.apply(port))
          .withConfig(BoltConnector.listen_address, new org.neo4j.configuration.helpers.SocketAddress("localhost", port))
          .withConfig(GraphDatabaseSettings.udc_enabled, false)
          // You can add custom procedures using .withProcedure()
          // .withProcedure(SampleProcedure.class)
          .build();
    //  embeddedDatabaseServer.printLogs(System.out);
    }
    return port;
  }

  public void stop() {
    if(embeddedDatabaseServer != null) {
      this.embeddedDatabaseServer.databaseManagementService().shutdown();
      this.embeddedDatabaseServer.close();
      this.embeddedDatabaseServer = null;
    }
  }

  public int getPort(){
    return port;
  }

  private int getFreePort() throws IOException {
    ServerSocket socket = new ServerSocket(0);
    int port = socket.getLocalPort();
    socket.close();
    return port;
  }

  /**
   * Sometimes needed for configuring a Neo4j Test Instance
   */
/*  public static DatabaseManagementService getManagementService() {
    return managementService;
  }*/
}
