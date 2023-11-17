/* (c) https://github.com/MontiCore/monticore */
package flensburg.database;

import hardy.flensburg.FlensburgManagerImpl;
import hardy.flensburg.HumanBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import umlp.backendrte.common.DatabaseAccessManager4Neo4j;

import java.nio.file.Paths;
import java.sql.SQLException;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

// Note: see TestDatabaseAccessManager.openWebServer to inspect the in-memory db for SQL
public abstract class GenAbstractDBTest {

  private DatabaseConnectionType dbType;

  private static DatabaseManagementService managementService = null;

  private static final int port = 7687;

  public GenAbstractDBTest(DatabaseConnectionType dbConnectionType) {
    this.dbType = dbConnectionType;
  }

  @BeforeEach
  public void init(){
    if(dbType == DatabaseConnectionType.SQL) {
      // init the DB with the schema
      TestDatabaseAccessManager.init();
      FlensburgManagerImpl.init();
    } else {
      DatabaseAccessManager4Neo4j.initBoltLocalhost(port, "neo4j", "");
      FlensburgManagerImpl.initNeo4J();
    }
  }

  @AfterEach
  public void cleanup() throws SQLException{

    if (dbType == DatabaseConnectionType.SQL) {
      // if GemIds in DB are deleted they also need to be locally deleted
      TestDatabaseAccessManager.clearLocalGemIds();
      TestDatabaseAccessManager.close();
    } else {
      this.cleanupNeo4j();
    }
  }

  //@BeforeAll
  public static void setUpLocalNeo4jInstance() {

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

  public void cleanupNeo4j() throws SQLException {
    // After each test set the database into a blank state again
    DatabaseAccessManager4Neo4j.executeStatement("MATCH(n) DETACH DELETE n");
    DatabaseAccessManager4Neo4j.closeConnection();
    DatabaseAccessManager4Neo4j.clearLocalGemIds();
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
  protected static class MockHumanBuilder extends HumanBuilder {
    // Accessible without FlensburgManager to test DAO without observers
    public MockHumanBuilder(long gemId) {
      super(gemId);
      // Dont automatically add to FlensburgManager on build
      // so we can test loading entities from db
      INTERNAL_deser_mode = true;
    }
  }

  protected enum DatabaseConnectionType {
    SQL,
    NEO4J
  }
}
