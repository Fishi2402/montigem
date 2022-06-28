package stanley.sync;

import de.se_rwth.commons.logging.Log;
import hardy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanley.FlensburgManagerClientImplBuilder;
import stanley.FlensburgTestContext;

import static org.junit.jupiter.api.Assertions.*;


public class PersonSyncTest {
  @BeforeEach
  public void clear(){
    FlensburgTestContext.clear();
    Log.initDEBUG();
  }

  @Test
  public void testMainResidenceIsSyncedToServer(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    // Create a Person
    clientContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Person p1001 = pb1001.mainResidence(addr).age(20).nickname("Smith").build().get();

    // Check that person is available in the server
    serverContext.activate();

    Person p1001Server = FlensburgManager.getPersonSure(startGemId);
    assertNotSame(p1001, p1001Server);
    assertEquals(startGemId, p1001Server.getGemId());
    assertEquals(20, p1001Server.getAge());
    assertTrue(p1001Server.availableMainResidence());

    Address addrServer = p1001Server.getMainResidence();
    assertNotSame(addr, addrServer);
    assertEquals(startGemId + 1, addrServer.getGemId());
    assertEquals("Flensburg", addrServer.getCity());
  }

  @Test
  public void testMainResidenceChangeIsSyncedToClient(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    // Create a Person
    clientContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Person p1001 = pb1001.mainResidence(addr).age(20).nickname("Smith").build().get();

    // Change address in server context
    serverContext.activate();

    Person p1001Server = FlensburgManager.getPersonSure(startGemId);
    assertNotNull(p1001Server);
    assertNotSame(p1001, p1001Server);

    Address a2 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    p1001Server.setMainResidence(a2);
    assertEquals(a2, p1001Server.getMainResidence());

    // Check that address was changed in client
    clientContext.activate();
    assertEquals("Aachen", p1001.getMainResidence().getCity());
  }

}
