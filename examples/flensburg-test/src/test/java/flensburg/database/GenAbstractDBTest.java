/* (c) https://github.com/MontiCore/monticore */
package flensburg.database;

import hardy.flensburg.FlensburgManagerImpl;
import hardy.flensburg.HumanBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

// Note: see TestDatabaseAccessManager.openWebServer to inspect the in-memory db
public abstract class GenAbstractDBTest {

  @BeforeEach
  public void init(){
    TestDatabaseAccessManager.init();
    FlensburgManagerImpl.init();
  }

  @AfterEach
  public void cleanup(){
    TestDatabaseAccessManager.close();
  }

  // TODO: same from the perspective of address


  protected static class MockHumanBuilder extends HumanBuilder {
    // Accessible without FlensburgManager to test DAO without observers
    public MockHumanBuilder(long gemId) {
      super(gemId);
      // Dont automatically add to FlensburgManager on build
      // so we can test loading entities from db
      INTERNAL_deser_mode = true;
    }
  }
}
