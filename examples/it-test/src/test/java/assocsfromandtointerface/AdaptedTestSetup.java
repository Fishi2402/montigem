/* (c) https://github.com/MontiCore/monticore */
package assocsfromandtointerface;

import assocsfromandtointerface.test_infrastructure.AssocsFromAndToInterfaceDBTestSetup;
import assocsfromandtointerface.test_infrastructure.AssocsFromAndToInterfaceMockTestSetup;
import assocsfromandtointerface.test_infrastructure.AssocsFromAndToInterfaceSQLDBTestSetup;
import umlp.backendrte.test_infrastructure.TestSetup;

public interface AdaptedTestSetup extends TestSetup<AssocsFromAndToInterfaceTestContext> {
  default void clearCaches(){
    for (int i = 0; i < getNumberOfClients(); i++) {
      getClients(i).getManager()._TESTING_clearCache();
    }
  }

  class MockTestSetup extends AssocsFromAndToInterfaceMockTestSetup implements AdaptedTestSetup {
    public MockTestSetup(int numberOfClients) {
      super(numberOfClients);
    }

    @Override
    public void clearCaches() {
      AdaptedTestSetup.super.clearCaches();
      // Mock holds data in-memory
      // => Can not clear cache of backend here, since there is nowhere to load it from
    }
  }

  class DbTestSetup extends AssocsFromAndToInterfaceSQLDBTestSetup implements AdaptedTestSetup{
    public DbTestSetup(int numberOfClients) {
      super(numberOfClients);
    }

    @Override
    public void clearCaches() {
      AdaptedTestSetup.super.clearCaches();
      backend.getManager()._TESTING_clearCache();
    }
  }

}
