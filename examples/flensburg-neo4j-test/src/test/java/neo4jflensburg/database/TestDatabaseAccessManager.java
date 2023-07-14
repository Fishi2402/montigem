/* (c) https://github.com/MontiCore/monticore */
package neo4jflensburg.database;

// GEN-T

import de.se_rwth.commons.logging.Log;
import umlp.backendrte.common.DatabaseAccessManager;
import umlp.backendrte.common.DatabaseAccessManager4Neo4j;

import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDatabaseAccessManager extends DatabaseAccessManager4Neo4j {

  private static long freeGemId = -1;

  public static void init(){
    instance = new TestDatabaseAccessManager();
    setup();
  }

  @Override
  protected void _setup() {
    try {
      DriverManager.registerDriver(new org.neo4j.jdbc.bolt.BoltDriver());
      // Files for Neo4j DB can be found in target/temporaryNeo4jDB it is started in the GenAbstractDBTest
      connection = DriverManager.getConnection("jdbc:neo4j:bolt://localhost:7687/?flatten=1", "neo4j", "");
    } catch (SQLException e) {
      Log.error("Error while initializing db", e);
    }
  }

  protected static void close(){
    try {
      // Delete DB
      connection.prepareStatement("MATCH(n) DETACH DELETE n").execute();
      if(connection != null && !connection.isClosed()){
        connection.close();
      }
    } catch (SQLException e) {
      Log.error("Error closing db", e);
    }
  }
}
