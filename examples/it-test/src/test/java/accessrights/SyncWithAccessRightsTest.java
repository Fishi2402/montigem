package accessrights;

import accessrights.test_infrastructure.AccessRightsMockTestSetup;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SyncWithAccessRightsTest extends AccessRightsMockTestSetup {
  public SyncWithAccessRightsTest() {
    super(2);
  }

  @Test
  public void testAllowedAccessIsSynced(){
    Foo fooServer = AccessRightsManager.fooBuilder().a(1).b(2).build().get();

    getClients(0).activate();
    Foo fooC1 = AccessRightsManager.getFooSure(fooServer.gemId);

    getClients(1).activate();
    Foo fooC2 = AccessRightsManager.getFooSure(fooServer.gemId);
    fooC2.setB(3);

    assertEquals(3, fooC1.getB());
    assertEquals(3, fooServer.getB());
  }

  @Disabled("Witness for https://git.rwth-aachen.de/monticore/monticore/-/issues/3561")
  @Test
  public void testDeniedAccessIsNotSynced(){
    Foo fooServer = AccessRightsManager.fooBuilder().a(1).b(2).build().get();

    getClients(0).activate();
    Foo fooC1 = AccessRightsManager.getFooSure(fooServer.gemId);

    getClients(1).activate();
    Foo fooC2 = AccessRightsManager.getFooSure(fooServer.gemId);
    fooC2.setA(3);

    assertEquals(1, fooC1.getB());
    assertEquals(1, fooServer.getB());
  }

}
