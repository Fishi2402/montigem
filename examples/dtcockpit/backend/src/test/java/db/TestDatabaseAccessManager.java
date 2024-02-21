/* (c) https://github.com/MontiCore/monticore */
package db;

import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import umlp.backendrte.common.DatabaseAccessManager4SQL;

import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class TestDatabaseAccessManager extends DatabaseAccessManager4SQL {

  public static void init(){
    instance = new TestDatabaseAccessManager();
    setup();
  }

  public static void clearPostgresDatabase() {
    executeStatement("DROP SCHEMA public CASCADE;");
    executeStatement("CREATE SCHEMA public;");
  }

  @Override
  protected void _setup() {
    try {
      // postgres db
      connection = DriverManager.getConnection("jdbc:postgresql://postgresql:5432/"+
          System.getenv("POSTGRES_DB")+"?user=" +  System.getenv("user") + "&password="+
          System.getenv("pass"));
    } catch (SQLException e) {
      Log.error("Error while initializing db", e);
    }
  }
}
