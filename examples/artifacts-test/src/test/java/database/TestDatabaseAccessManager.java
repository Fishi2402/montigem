package database;/* (c) https://github.com/MontiCore/monticore */

// GEN-T

import de.se_rwth.commons.logging.Log;
import umlp.backendrte.common.DatabaseAccessManager;
import umlp.backendrte.common.DatabaseAccessManager4Neo4j;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestDatabaseAccessManager extends DatabaseAccessManager4Neo4j {

  public static void init () {
    TestDatabaseAccessManager impl = new TestDatabaseAccessManager();
    DatabaseAccessManager.setInstance(impl);
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
}
