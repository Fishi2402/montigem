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

  @Override
  protected void _setup() {
    try {
      // postgres db
      connection = DriverManager.getConnection("jdbc:postgresql://postgres:5432/"+ System.getenv("POSTGRES_DB"));
      String schemaFileName = "Dtcockpit.sql";
      Optional<URL> optionalURL = FileReaderWriter.getResource(this.getClass().getClassLoader(), schemaFileName);
      if(optionalURL.isEmpty()) {
        Log.error("0xAAAAAA: Could not find schema "+schemaFileName+" for database.");
      }
      String schema = FileReaderWriter.readFromFile(optionalURL.get());
      initSchema(schema);
    } catch (SQLException e) {
      Log.error("Error while initializing db", e);
    }
  }
}
