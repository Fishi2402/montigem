package stanley.sync;

import de.se_rwth.commons.logging.Log;
import hardy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanley.FlensburgManagerClientImplBuilder;
import stanley.FlensburgTestContext;
import umlp.command.TestClientCommandManager;
import umlp.command.Command;
import umlp.command.CommandResult;
import umlp.request.GetFreeGemIdRequest;
import umlp.request.GetFreeGemListIdRequest;
import umlp.rte.GemList;

import java.util.ArrayList;
import java.util.List;

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

    for(int i = 0; i < 10; i++) {
      Log.info("Add Car " + i + " from Client", "PersonSyncTest");
      clientContext.activate();
      Car c_i = FlensburgManager.carBuilder().owns(p1001).build().get();

      Log.info("Check it is synced to server", "PersonSyncTest");
      serverContext.activate();
      assertEquals(i + 1, p1001Server.sizeOwnss());
      Car c_i_server = p1001Server.getOwns(i);
      assertNotNull(c_i_server);
      assertNotSame(c_i, c_i_server);
      assertEquals(c_i.getGemId(), c_i_server.getGemId());
    }
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

    for (int i = 0; i < 10; i++) {
      Log.info("Add Car " + i + " from Server", "PersonSyncTest");
      serverContext.activate();
      Car c_i_server = FlensburgManager.carBuilder().owns(p1001Server).build().get();

      Log.info("Check it is synced to the client", "PersonSyncTest");
      clientContext.activate();
      assertEquals(i + 1, p1001.sizeOwnss());
      Car c_i_client = p1001.getOwns(i);
      assertNotNull(c_i_client);
      assertNotSame(c_i_server, c_i_client);
      assertEquals(c_i_server.getGemId(), c_i_client.getGemId());
    }
  }

  @Test
  public void testExistingOwsIsLoadedOnClient(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Create a Person @ server", "PersonSyncTest");
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Person p1001 = pb1001.mainResidence(addr).age(20).nickname("Smith").build().get();

    List<Car> cars = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      Log.info("Create Car " + i, "PersonSyncTest");
      Car c = FlensburgManager.carBuilder().owns(p1001).build().get();
      cars.add(c);
    }

    // Check that all cars are synced to the client
    clientContext.activate();
    Person p1001Client = FlensburgManager.getPersonSure(startGemId);
    assertEquals(cars.size(), p1001Client.sizeOwnss());

    for (int i = 0; i < cars.size(); i++) {
      Car c1 = cars.get(i);
      Car c2 = p1001Client.getOwns(i);
      assertEquals(c1.getGemId(), c2.getGemId());
      assertNotSame(c1, c2);
    }
  }

  @Test
  public void testGemListSyncedToClientWithoutContent(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Create a Person with car @ server", "PersonSyncTest");
    serverContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Car c = FlensburgManager.carBuilder().owns(pb1001).build().get();
    Person p1001 = pb1001.mainResidence(addr).age(20).nickname("Smith").build().get();

    assertTrue(c.isBuildFinished());

    Log.info("Load Person in client", "PersonSyncTest");
    clientContext.activate();
    Person p1001Client = FlensburgManager.getPersonSure(startGemId);
    assertNotNull(p1001Client);
    assertNotSame(p1001, p1001Client);

    Log.info("Assert that GemList owns was transferred empty", "PersonSyncTest");
    GemList<Car> owns = p1001Client.getOwnsGemList();
    assertEquals(0, owns.sizeLocallyKnownIds());

    Log.info("Assert that GemList will correctly load elements on demand", "PersonSyncTest");
    assertEquals(1, owns.size());

    Car cClient = owns.get(0);
    assertNotSame(c, cClient);
    assertEquals(c.getGemId(), cClient.getGemId());
  }

  @Test
  public void testGemListNotSyncedWhileBuilding(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder()
        .backendContext(serverContext)
        .commandManager(new TestClientCommandManager(serverContext){
          @Override
          protected <T> CommandResult<T> _executeCommand(Command<T> command) {
            if(!(command instanceof GetFreeGemIdRequest || command instanceof GetFreeGemListIdRequest)){
              fail("Received command that is not a GetFreeGemIdRequest: " + command);
            }
            return super._executeCommand(command);
          }
        })
        .build()
        .get();

    Log.info("Using PersonBuilder with CarBuilder @ client", "PersonSyncTest");
    clientContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    CarBuilder c = FlensburgManager.carBuilder();
    pb1001.ownsAdd(c);
  }

  @Test
  public void testRemoveFromGemListInClient(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Create a Person with car @ server", "PersonSyncTest");
    serverContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Car c = FlensburgManager.carBuilder().owns(pb1001).build().get();
    Person p1001 = pb1001.mainResidence(addr).age(20).nickname("Smith").build().get();

    clientContext.activate();
    Person p1001Client = FlensburgManager.getPersonSure(startGemId);
    assertNotSame(p1001, p1001Client);
    assertEquals(1, p1001Client.sizeOwnss());

    p1001Client.removeOwns(0);
    assertEquals(0, p1001Client.sizeOwnss());

    serverContext.activate();
    assertEquals(0, p1001.sizeOwnss());
  }


  @Test
  public void testRemoveFromGemListInServer(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Create a Person with car @ server", "PersonSyncTest");
    serverContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Car c = FlensburgManager.carBuilder().owns(pb1001).build().get();
    Person p1001 = pb1001.mainResidence(addr).age(20).nickname("Smith").build().get();

    clientContext.activate();
    Person p1001Client = FlensburgManager.getPersonSure(startGemId);
    assertNotSame(p1001, p1001Client);
    assertEquals(1, p1001Client.sizeOwnss());

    serverContext.activate();
    p1001.removeOwns(c);
    assertEquals(0, p1001.sizeOwnss());

    clientContext.activate();
    assertEquals(0, p1001Client.sizeOwnss());
  }
}
