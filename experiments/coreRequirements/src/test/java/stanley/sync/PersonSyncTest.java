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

    Log.info("Create a Person", "PersonSyncTest");
    clientContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Person p1001 = pb1001.mainResidence(addr).age(20).nickname("Smith").build().get();

    Log.info("Check that person is available in the server", "PersonSyncTest");
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

    Log.info("Create a Person", "PersonSyncTest");
    clientContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Person p1001 = pb1001.mainResidence(addr).age(20).nickname("Smith").build().get();

    Log.info("Change address in server context", "PersonSyncTest");
    serverContext.activate();

    Person p1001Server = FlensburgManager.getPersonSure(startGemId);
    assertNotNull(p1001Server);
    assertNotSame(p1001, p1001Server);

    Address a2 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    p1001Server.setMainResidence(a2);
    assertEquals(a2, p1001Server.getMainResidence());

    Log.info("Check that address was changed in client", "PersonSyncTest");
    clientContext.activate();
    assertEquals("Aachen", p1001.getMainResidence().getCity());
  }

  @Test
  public void testAddOwnsIsSyncedToServer(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Create a Person @ client", "PersonSyncTest");
    clientContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Person p1001 = pb1001.mainResidence(addr).age(20).nickname("Smith").build().get();

    Log.info("Check owns is empty in Server", "PersonSyncTest");
    serverContext.activate();
    Person p1001Server = FlensburgManager.getPersonSure(startGemId);
    assertNotSame(p1001, p1001Server);
    assertTrue(p1001Server.isEmptyOwnss());

    Log.info("Add Car from Client", "PersonSyncTest");
    clientContext.activate();
    Car c1 = FlensburgManager.carBuilder().owns(p1001).build().get();

    Log.info("Check it is synced to server", "PersonSyncTest");
    serverContext.activate();
    assertEquals(1, p1001Server.sizeOwnss());
    Car c1Server = p1001Server.getOwns(0);
    assertNotNull(c1Server);
    assertNotSame(c1, c1Server);
    assertEquals(c1.getGemId(), c1Server.getGemId());
  }

  @Test
  public void testAddOwnsIsSyncedToClient(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Create a Person @ client", "PersonSyncTest");
    clientContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Person p1001 = pb1001.mainResidence(addr).age(20).nickname("Smith").build().get();

    Log.info("Check owns is empty in Server", "PersonSyncTest");
    serverContext.activate();
    Person p1001Server = FlensburgManager.getPersonSure(startGemId);
    assertNotSame(p1001, p1001Server);
    assertTrue(p1001Server.isEmptyOwnss());

    Log.info("Add Car from Server", "PersonSyncTest");
    serverContext.activate();
    Car c1Server = FlensburgManager.carBuilder().owns(p1001Server).build().get();

    Log.info("Check it is synced to the client", "PersonSyncTest");
    clientContext.activate();
    assertEquals(1, p1001.sizeOwnss());
    Car c1Client = p1001.getOwns(0);
    assertNotNull(c1Client);
    assertNotSame(c1Server, c1Client);
    assertEquals(c1Server.getGemId(), c1Client.getGemId());
  }

}
