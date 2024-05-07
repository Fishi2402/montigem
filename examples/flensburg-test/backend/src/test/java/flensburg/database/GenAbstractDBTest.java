/* (c) https://github.com/MontiCore/monticore */
package flensburg.database;

import hardy.flensburg.HumanBuilder;
import hardy.flensburg.test_infrastructure.FlensburgSQLDBTestSetup;
import umlp.backendrte.test_infrastructure.ClientSetup;

// Note: see TestDatabaseAccessManager.openWebServer to inspect the in-memory db for SQL
public abstract class GenAbstractDBTest extends FlensburgSQLDBTestSetup {

  public GenAbstractDBTest(ClientSetup clientSetup) {
    super(clientSetup);
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
