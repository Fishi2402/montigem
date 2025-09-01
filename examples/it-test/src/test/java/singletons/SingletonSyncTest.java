package singletons;

import org.junit.jupiter.api.Test;
import singletons.test_infrastructure.SingletonsMockTestSetup;
import singletons.test_infrastructure.SingletonsSQLDBTestSetup;
import umlp.test_rte.ClientSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingletonSyncTest extends SingletonsSQLDBTestSetup {

  public SingletonSyncTest() {
    super(1);
  }

  @Test
  public void testGetSingletonWithoutGemIdTriggersSync(){
    getClients(0).activate();

    FooSingleton fsC1 = SingletonsManager.getFooSingleton();
    assertEquals(0, fsC1.getI());

    getBackend().activate();
    FooSingleton fsB = SingletonsManager.getFooSingleton();
    assertEquals(fsB.getGemId(), fsC1.getGemId());
    fsB.setI(1);

    getClients(0).activate();
    assertEquals(1, fsC1.getI());
  }
}
