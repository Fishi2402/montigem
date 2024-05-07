/* (c) https://github.com/MontiCore/monticore */

package database;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.neo4j.dbms.api.DatabaseManagementService;

import java.sql.SQLException;

//import org.neo4j.dbms.api.DatabaseManagementService;

public abstract class GenAbstractNeo4jDBTest {
  @BeforeEach
  public void init(){
    Log.init();
    TestDatabaseAccessManagerSQL.init();
  }

  @AfterEach
  public void cleanup() throws SQLException {
    TestDatabaseAccessManagerSQL.closeConnection();
    TestDatabaseAccessManagerSQL.clearLocalGemIds();
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
