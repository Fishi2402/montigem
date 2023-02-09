/* (c) https://github.com/MontiCore/monticore */
package flensburg.database;

// GEN-T

import com.google.common.io.Resources;
import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import org.h2.tools.Server;
import umlp.rte.DatabaseAccessManager;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class TestDatabaseAccessManager extends DatabaseAccessManager {

  public static void init(){
    instance = new TestDatabaseAccessManager();
  }

  @Override
  protected void _setup() {
    try {
      DriverManager.registerDriver(new org.h2.Driver());
      // In memory db
      // DB_CLOSE_DELAY=0 -> In memory db is deleted after connection is closed
      connection = DriverManager.getConnection("jdbc:h2:mem:test_flensburg;DB_CLOSE_DELAY=0");
      Optional<URL> optionalURL = FileReaderWriter.getResource(this.getClass().getClassLoader(), "Flensburg.sql");
      if(optionalURL.isEmpty()) {
        Log.error("0xAAAAAA Could not find schema for database.");
      }
      String schema = FileReaderWriter.readFromFile(optionalURL.get());
      initSchema(schema);
    } catch (SQLException e) {
      Log.error("Error while initializing db", e);
    }
  }

  protected static void close(){
    try {
      if(connection != null && !connection.isClosed()){
        connection.close();
      }
    } catch (SQLException e) {
      Log.error("Error closing db", e);
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
