package hardy.database;

import hardy.FlensburgManagerImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimpleDBTest {

  @BeforeEach
  public void init(){
    TestDatabaseAccessManager.init();
    FlensburgManagerImpl.init();
  }

  @AfterEach
  public void cleanup(){
    TestDatabaseAccessManager.close();
  }

  // TODO think of a good way for testing DBs -> use h2 Database
  @Test
  public void testSaveAndLoadDB() {
    // TODO AHe: continue here
    // TODO: impl get/create human/person to test
  }
}
