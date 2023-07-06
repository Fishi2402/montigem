package database;/* (c) https://github.com/MontiCore/monticore */

import baseartifactstest.BaseArtifactsTestManagerImpl;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.driver.Driver;

import java.nio.file.Paths;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

//import org.neo4j.dbms.api.DatabaseManagementService;

public abstract class GenAbstractNeo4jDBTest {

  private Driver driver;

  private static DatabaseManagementService managementService = null;

/*  @BeforeAll
  public static void initDB() {
    if(managementService != null) {
      managementService.database(DEFAULT_DATABASE_NAME);
      return;
    }
    managementService =
        new DatabaseManagementServiceBuilder(Paths.get("target", "temporaryNeo4jDB"))
            .setConfig(BoltConnector.enabled, true )
            .setConfig(BoltConnector.listen_address, new SocketAddress("localhost", 7688 )).build();
    registerShutdownHook(managementService);
    managementService.database(DEFAULT_DATABASE_NAME);
  }*/

  @BeforeEach
  public void init(){
    Log.init();
    TestDatabaseAccessManagerSQL.init();
  }

  @AfterEach
  public void cleanup(){
    TestDatabaseAccessManagerSQL.close();
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
