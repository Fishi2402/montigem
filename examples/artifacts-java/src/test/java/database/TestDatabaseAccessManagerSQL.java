/* (c) https://github.com/MontiCore/monticore */
package database;

// GEN-T

import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import org.h2.tools.Server;
import umlp.backendrte.common.DatabaseAccessManager;
import umlp.backendrte.common.DatabaseAccessManager4SQL;

import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class TestDatabaseAccessManagerSQL extends DatabaseAccessManager4SQL {

  public static void init(){
    instance = new TestDatabaseAccessManagerSQL();
    setup();
  }

  @Override
  protected void _setup() {
    try {
      DriverManager.registerDriver(new org.h2.Driver());
      // In memory db
      // DB_CLOSE_DELAY=0 -> In memory db is deleted after connection is closed
      connection = DriverManager.getConnection("jdbc:h2:mem:test_flensburg;DB_CLOSE_DELAY=0");
      String schemaFileName = "JavaProject.sql";
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

  /*
   * Will open the H2 web console that can be used to inspect the current DB state
   * and BLOCK THE THREAD.
   * To continue program execution press disconnect in the web console.
   */
  protected static void openWebServer() {
    try {
      Server.startWebServer(connection);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
