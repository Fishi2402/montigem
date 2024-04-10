package umlp.backendrte.common;

import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Can be used to start a Neo4j instance from this program.
 * Should only be used for short-lived instances, like tests.
 * Permanent deployments should probably use a neo4j container
 */
// TODO: current setup requires one db per project
// TODO: tests for each project are run in a JVM => incrementing the port is not possible in a var
// TODO: Alternative: find empty port via serverport
public class Neo4jDatabaseStarter {
  protected static DatabaseManagementService managementService;
  protected static AtomicInteger port = new AtomicInteger(7689);

  public void start(Path dbBasePath){
    if (managementService != null) {
      managementService.database(GraphDatabaseSettings.DEFAULT_DATABASE_NAME);
      return;
    }
    managementService = new DatabaseManagementServiceBuilder(dbFilePath)
        .setConfig(BoltConnector.enabled, true)
        .setConfig(BoltConnector.listen_address, new SocketAddress("localhost", port)).build();
    registerShutdownHook(managementService);
    managementService.database(GraphDatabaseSettings.DEFAULT_DATABASE_NAME);
  }

  private static void registerShutdownHook( final DatabaseManagementService managementService )
  {
    // Registers a shutdown hook for the Neo4j instance so that it
    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
    // running application).
    Runtime.getRuntime().addShutdownHook( new Thread()
    {
      @Override
      public void run()
      {
        managementService.shutdown();
      }
    } );
  }
}
