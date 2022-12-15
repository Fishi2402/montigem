package hardy.database;

// GEN-T

import com.google.common.io.Resources;
import de.se_rwth.commons.logging.Log;
import umlp.rte.DatabaseAccessManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

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
      String schema = Resources.toString(Objects.requireNonNull(getClass().getResource("/Flensburg.sql")), StandardCharsets.UTF_8);
      connection.createStatement().execute(schema);
    } catch (SQLException | IOException e) {
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

}
