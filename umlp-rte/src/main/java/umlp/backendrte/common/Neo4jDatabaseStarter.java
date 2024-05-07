package umlp.backendrte.common;

import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * Can be used to start a Neo4j instance from this program.
 * Should only be used for short-lived instances, like tests.
 * Permanent deployments should probably use a neo4j container
 */
public class Neo4jDatabaseStarter {
  protected static DatabaseManagementService managementService;
  protected static int port = -1;

  public int start(Function<Integer, Path> portToDBBasePath) throws IOException {
    if (managementService != null) {
      managementService.database(GraphDatabaseSettings.DEFAULT_DATABASE_NAME);
      return port;
    }
    port = getFreePort();
    managementService = new DatabaseManagementServiceBuilder(portToDBBasePath.apply(port))
        .setConfig(BoltConnector.enabled, true)
        .setConfig(BoltConnector.listen_address, new SocketAddress("localhost", port)).build();
    registerShutdownHook(managementService);
    managementService.database(GraphDatabaseSettings.DEFAULT_DATABASE_NAME);
    return port;
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

  private static void registerShutdownHook( final DatabaseManagementService managementService )
  {
    // Registers a shutdown hook for the Neo4j instance so that it
    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
    // running application).
    Runtime.getRuntime().addShutdownHook(new Thread(managementService::shutdown));
  }
}
