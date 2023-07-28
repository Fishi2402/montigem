/* (c) https://github.com/MontiCore/monticore */
package neo4jflensburg.database;

import neo4jflensburg.Neo4jFlensburgManagerImpl;
import neo4jflensburg.Neo4jHumanBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.driver.Driver;
import umlp.backendrte.common.DatabaseAccessManager4Neo4j;

import java.nio.file.Paths;
import java.sql.SQLException;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

//import org.neo4j.dbms.api.DatabaseManagementService;

public abstract class GenAbstractNeo4jDBTest {

  private static DatabaseManagementService managementService = null;

  private static final int port = 7687;

  @BeforeAll
  public static void initDB() {
    if(managementService != null) {
      managementService.database(DEFAULT_DATABASE_NAME);
      return;
    }
    managementService =
        new DatabaseManagementServiceBuilder(Paths.get("build", "temporaryNeo4jDB"))
            .setConfig(BoltConnector.enabled, true )
            .setConfig(BoltConnector.listen_address, new SocketAddress("localhost", port )).build();
    registerShutdownHook(managementService);
    managementService.database(DEFAULT_DATABASE_NAME);
  }

  @BeforeEach
  public void init(){
    DatabaseAccessManager4Neo4j.initBoltLocalhost(port, "neo4j", "");
    Neo4jFlensburgManagerImpl.init();
  }

  @AfterEach
  public void cleanup() throws SQLException {
    // After each test set the database into a blank state again
    DatabaseAccessManager4Neo4j.executeStatement("MATCH(n) DETACH DELETE n");
    DatabaseAccessManager4Neo4j.closeConnection();
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

  // TODO: same from the perspective of address
  protected static class MockHumanBuilder extends Neo4jHumanBuilder {
    // Accessible without FlensburgManager to test DAO without observers
    public MockHumanBuilder(long gemId) {
      super(gemId);
      // Dont automatically add to FlensburgManager on build
      // so we can test loading entities from db
      INTERNAL_deser_mode = true;
    }
  }
}
