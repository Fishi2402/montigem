package hardy.database;

import hardy.*;
import org.hibernate.Session;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import umlp.rte.DatabaseAccessManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class SimpleDBTest {

  // TODO think of a good way for testing DBs -> use h2 Database
  @Disabled
  @Test
  public void testSaveAndLoadDB() {

    DatabaseAccessManager.setup();
    DatabaseAccessManager.executeSQLUpdate("CREATE SCHEMA meinSchema");
  }
}
