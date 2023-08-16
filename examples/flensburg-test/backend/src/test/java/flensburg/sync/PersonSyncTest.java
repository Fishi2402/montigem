/* (c) https://github.com/MontiCore/monticore */
package flensburg.sync;

import de.se_rwth.commons.logging.Log;

import flensburg.infrastructure.FlensburgManagerClientImplBuilder;
import hardy.flensburg.Address;
import hardy.flensburg.Car;
import hardy.flensburg.CarBuilder;
import hardy.flensburg.FlensburgManager;
import hardy.flensburg.FlensburgMock;
import hardy.flensburg.FlensburgTestContext;
import hardy.flensburg.Person;
import hardy.flensburg.PersonBuilder;
import hardy.flensburg.requests.GetFreeGemIdRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandDirectCommunication;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.command.TestClientCommandManager;
import umlp.backendrte.common.GemList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
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
    assertTrue(p1001Server.getMainResidenceGem().isPresent());
    assertEquals("Flensburg", p1001Server.getMainResidence().getCity());

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
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
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
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
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
    assertTrue(p1001Server.getOwns().isEmpty());

    for(int i = 0; i < 10; i++) {
      Log.info("Add Car " + i + " from Client", "PersonSyncTest");
      clientContext.activate();
      Car c_i = FlensburgManager.carBuilder().wheelLeft(true).build().get();
      p1001.addOwns(c_i);

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
  public void testAddOwnsIsSyncedToServerWithListInterface(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Create a Person @ client", "PersonSyncTest");
    clientContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Person p1001 = pb1001.mainResidence(addr).age(20).nickname("Smith").build().get();
    List<Car> p1001Owns = p1001.getOwns();

    Log.info("Check owns is empty in Server", "PersonSyncTest");
    serverContext.activate();
    Person p1001Server = FlensburgManager.getPersonSure(startGemId);
    List<Car> p1001ServerOwns = p1001Server.getOwns();
    assertNotSame(p1001, p1001Server);
    assertNotSame(p1001Owns, p1001ServerOwns);
    assertTrue(p1001ServerOwns.isEmpty());

    for(int i = 0; i < 10; i++) {
      Log.info("Add Car " + i + " from Client", "PersonSyncTest");
      clientContext.activate();
      Car c_i = FlensburgManager.carBuilder().wheelLeft(true).build().get();
      p1001Owns.add(c_i);

      Log.info("Check it is synced to server", "PersonSyncTest");
      serverContext.activate();
      assertEquals(i + 1, p1001ServerOwns.size());
      Car c_i_server = p1001ServerOwns.get(i);
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
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
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
    assertTrue(p1001Server.getOwns().isEmpty());

    for (int i = 0; i < 10; i++) {
      Log.info("Add Car " + i + " from Server", "PersonSyncTest");
      serverContext.activate();
      Car c_i_server = FlensburgManager.carBuilder().wheelLeft(true).build().get();
      p1001Server.addOwns(c_i_server);

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
  public void testAddOwnsIsSyncedToClientWithListInterface(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Create a Person @ client", "PersonSyncTest");
    clientContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Person p1001 = pb1001.mainResidence(addr).age(20).nickname("Smith").build().get();
    List<Car> p1001Owns = p1001.getOwns();

    Log.info("Check owns is empty in Server", "PersonSyncTest");
    serverContext.activate();
    Person p1001Server = FlensburgManager.getPersonSure(startGemId);
    List<Car> p1001ServerOwns = p1001Server.getOwns();
    assertNotSame(p1001, p1001Server);
    assertTrue(p1001ServerOwns.isEmpty());

    for (int i = 0; i < 10; i++) {
      Log.info("Add Car " + i + " from Server", "PersonSyncTest");
      serverContext.activate();
      Car c_i_server = FlensburgManager.carBuilder().wheelLeft(true).build().get();
      p1001ServerOwns.add(c_i_server);

      Log.info("Check it is synced to the client", "PersonSyncTest");
      clientContext.activate();
      assertEquals(i + 1, p1001.getOwns().size());
      Car c_i_client = p1001Owns.get(i);
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
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Create a Person @ server", "PersonSyncTest");
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Person p1001 = pb1001.mainResidence(addr).age(20).nickname("Smith").build().get();

    List<Car> cars = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      Log.info("Create Car " + i, "PersonSyncTest");
      Car c = FlensburgManager.carBuilder().wheelLeft(true).build().get();
      p1001.addOwns(c);
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
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Create a Person with car @ server", "PersonSyncTest");
    serverContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Car c = FlensburgManager.carBuilder().wheelLeft(true).build().get();
    Person p1001 = pb1001.ownsAdd(c).mainResidence(addr).age(20).nickname("Smith").build().get();

    assertTrue(c.isBuildFinished());

    Log.info("Load Person in client", "PersonSyncTest");
    clientContext.activate();
    Person p1001Client = FlensburgManager.getPersonSure(startGemId);
    assertNotNull(p1001Client);
    assertNotSame(p1001, p1001Client);

    Log.info("Assert that GemList will correctly load elements on demand", "PersonSyncTest");
    GemList<Car> owns = p1001Client._internal_getOwnsGemList();
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
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    CommandDirectCommunication commandCommunication = new CommandDirectCommunication(serverContext);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder()
        .backendContext(serverContext)
        .commandManager(new TestClientCommandManager(commandCommunication){
          @Override
          protected <T> CommandResult<T> _executeCommand(Command<T> command) {
            if(!(command instanceof GetFreeGemIdRequest)){
              fail("Received command that is not a GetFreeGemIdRequest: " + command);
            }
            return super._executeCommand(command);
          }
        })
        .build()
        .get();
    commandCommunication.setClientContext(clientContext);

    Log.info("Using PersonBuilder with CarBuilder @ client", "PersonSyncTest");
    clientContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    CarBuilder c = FlensburgManager.carBuilder().wheelLeft(true);
    pb1001.ownsAdd(c);
  }

  @Test
  public void testBuilderOwnsIsSyncedToServer(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Create a Person @ client", "PersonSyncTest");
    clientContext.activate();

    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    pb1001.mainResidence(addr).age(20).nickname("Smith");

    FlensburgMock.secretlyAdaptNextGemId(200);
    Log.info("Add Car from Client", "PersonSyncTest");
    clientContext.activate();
    Car c = FlensburgManager.carBuilder().wheelLeft(true).build().get();
    pb1001.ownsAdd(c);
    Person p1001 = pb1001.build().get();

    Log.info("Check owns is not empty in Server", "PersonSyncTest");
    serverContext.activate();
    Person p1001Server = FlensburgManager.getPersonSure(startGemId);
    assertNotSame(p1001, p1001Server);
    assertEquals(1, p1001Server.sizeOwnss());
  }

  @Test
  public void testRemoveFromGemListInClient(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Create a Person with car @ server", "PersonSyncTest");
    serverContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Car c = FlensburgManager.carBuilder().wheelLeft(true).build().get();
    Person p1001 = pb1001.ownsAdd(c).mainResidence(addr).age(20).nickname("Smith").build().get();

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
  public void testRemoveFromGemListInClientWithListInterface(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Create a Person with car @ server", "PersonSyncTest");
    serverContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Car c = FlensburgManager.carBuilder().wheelLeft(true).build().get();
    pb1001.getOwns().add(c);
    Person p1001 = pb1001.mainResidence(addr).age(20).nickname("Smith").build().get();
    List<Car> p1001Owns = p1001.getOwns();

    clientContext.activate();
    Person p1001Client = FlensburgManager.getPersonSure(startGemId);
    List<Car> p1001ClientOwns = p1001Client.getOwns();
    assertNotSame(p1001Owns, p1001ClientOwns);
    assertEquals(1, p1001ClientOwns.size());

    p1001ClientOwns.remove(0);
    assertEquals(0, p1001ClientOwns.size());

    serverContext.activate();
    assertEquals(0, p1001Owns.size());
  }

  @Test
  public void testRemoveFromGemListInServer(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Create a Person with car @ server", "PersonSyncTest");
    serverContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Car c = FlensburgManager.carBuilder().wheelLeft(true).build().get();
    Person p1001 = pb1001.ownsAdd(c).mainResidence(addr).age(20).nickname("Smith").build().get();

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

  @Test
  public void testRemoveFromGemListInServerWithListInterface(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Create a Person with car @ server", "PersonSyncTest");
    serverContext.activate();
    PersonBuilder pb1001 = FlensburgManager.personBuilder();
    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Car c = FlensburgManager.carBuilder().wheelLeft(true).build().get();
    pb1001.getOwns().add(c);
    Person p1001 = pb1001.mainResidence(addr).age(20).nickname("Smith").build().get();
    List<Car> p1001Owns = p1001.getOwns();

    clientContext.activate();
    Person p1001Client = FlensburgManager.getPersonSure(startGemId);
    List<Car> p1001ClientOwns = p1001Client.getOwns();
    assertNotSame(p1001Owns, p1001ClientOwns);
    assertEquals(1, p1001ClientOwns.size());

    serverContext.activate();
    p1001Owns.remove(c);
    assertEquals(0, p1001Owns.size());

    clientContext.activate();
    assertEquals(0, p1001ClientOwns.size());
  }

  @Test
  public void testVisitedIsSyncedToServerInBuilder(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(200);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Building Person with visited Addresses in Client", "PersonSyncTest");
    clientContext.activate();

    Address a1 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    Address a2 = FlensburgManager.addressBuilder().city("Flensburg").build().get();

    Log.info("Building Person", "PersonSyncTest");
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    Person p1001Client = FlensburgManager.personBuilder()
        .age(20)
        .nickname("Test")
        .mainResidence(a1)
        .visitedAdd(a1)
        .visitedAdd(a2)
        .build().get();

    Log.info("Checking it is available in the Server", "PersonSyncTest");
    serverContext.activate();
    Person p1001Server = FlensburgManager.getPersonSure(1001);
    assertNotSame(p1001Server, p1001Client);
    assertEquals(2, p1001Server.sizeVisiteds());

    Address visited1 = p1001Server.getVisited(0);
    Address visited2 = p1001Server.getVisited(1);
    assertEquals(200, visited1.getGemId());
    assertEquals(201, visited2.getGemId());

    assertEquals(1, visited1.sizeVisitors());
    assertEquals(1, visited2.sizeVisitors());
  }

  @Test
  public void testVisitedIsSyncedToServerInBuilderWithListInterface(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(200);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Building Person with visited Addresses in Client", "PersonSyncTest");
    clientContext.activate();

    Address a1 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    Address a2 = FlensburgManager.addressBuilder().city("Flensburg").build().get();

    Log.info("Building Person", "PersonSyncTest");
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    PersonBuilder pb1001Client = FlensburgManager.personBuilder()
        .age(20)
        .nickname("Test")
        .mainResidence(a1);
    pb1001Client.getVisited().addAll(List.of(a1, a2));
    Person p1001Client = pb1001Client.build().get();

    Log.info("Checking it is available in the Server", "PersonSyncTest");
    serverContext.activate();
    Person p1001Server = FlensburgManager.getPersonSure(1001);
    List<Address> p1001ServerVisited = p1001Server.getVisited();
    assertNotSame(p1001ServerVisited, p1001Client.getVisited());
    assertEquals(2, p1001ServerVisited.size());

    Address visited1 = p1001ServerVisited.get(0);
    Address visited2 = p1001ServerVisited.get(1);
    assertEquals(200, visited1.getGemId());
    assertEquals(201, visited2.getGemId());

    assertEquals(1, visited1.getVisitor().size());
    assertEquals(1, visited2.getVisitor().size());
  }

  @Test
  public void testVisitedIsSyncedToServerAfterBuilding(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(200);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Building Person with visited Addresses in Client", "PersonSyncTest");
    clientContext.activate();

    Address a1 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    Address a2 = FlensburgManager.addressBuilder().city("Flensburg").build().get();

    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    Person p1001Client = FlensburgManager.personBuilder()
        .age(20)
        .nickname("Test")
        .mainResidence(a1)
        .build().get();

    Log.info("Adding address 1", "PersonSyncTest");
    p1001Client.addVisited(a1);

    Log.info("Checking it is available in the Server", "PersonSyncTest");
    serverContext.activate();
    Person p1001Server = FlensburgManager.getPersonSure(1001);
    assertNotSame(p1001Server, p1001Client);
    System.out.println("Checking for person " + p1001Server);
    assertEquals(1, p1001Server.sizeVisiteds());

    Address visited1 = p1001Server.getVisited(0);
    assertEquals(200, visited1.getGemId());
    assertEquals(1, visited1.sizeVisitors());

    Log.info("Adding address 2", "PersonSyncTest");
    clientContext.activate();
    p1001Client.addVisited(a2);

    Log.info("Checking it is available in the Server", "PersonSyncTest");
    serverContext.activate();
    Address visited2 = p1001Server.getVisited(1);
    assertEquals(201, visited2.getGemId());
    assertEquals(1, visited2.sizeVisitors());
  }

  @Test
  public void testVisitedIsSyncedToServerAfterBuildingWithListInterface(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(200);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Building Person with visited Addresses in Client", "PersonSyncTest");
    clientContext.activate();

    Address a1 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    Address a2 = FlensburgManager.addressBuilder().city("Flensburg").build().get();

    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    Person p1001Client = FlensburgManager.personBuilder()
        .age(20)
        .nickname("Test")
        .mainResidence(a1)
        .build().get();

    Log.info("Adding address 1", "PersonSyncTest");
    p1001Client.getVisited().add(a1);

    Log.info("Checking it is available in the Server", "PersonSyncTest");
    serverContext.activate();
    Person p1001Server = FlensburgManager.getPersonSure(1001);
    assertNotSame(p1001Server, p1001Client);
    System.out.println("Checking for person " + p1001Server);
    assertEquals(1, p1001Server.getVisited().size());

    Address visited1 = p1001Server.getVisited().get(0);
    assertEquals(200, visited1.getGemId());
    assertEquals(1, visited1.getVisitor().size());

    Log.info("Adding address 2", "PersonSyncTest");
    clientContext.activate();
    p1001Client.getVisited().add(a2);

    Log.info("Checking it is available in the Server", "PersonSyncTest");
    serverContext.activate();
    Address visited2 = p1001Server.getVisited().get(1);
    assertEquals(201, visited2.getGemId());
    assertEquals(1, visited2.getVisitor().size());
  }

  @Test
  public void testVisitedRemovalFromServer(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(200);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Building Person with visited Addresses in Client", "PersonSyncTest");
    clientContext.activate();

    Address a1 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    Address a2 = FlensburgManager.addressBuilder().city("Flensburg").build().get();

    Log.info("Building Person", "PersonSyncTest");
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    Person p1001Client = FlensburgManager.personBuilder()
        .age(20)
        .nickname("Test")
        .mainResidence(a1)
        .visitedAdd(a1)
        .visitedAdd(a2)
        .build().get();

    Log.info("Checking it is removable in the Server", "PersonSyncTest");
    serverContext.activate();
    Person p1001Server = FlensburgManager.getPersonSure(1001);
    assertNotSame(p1001Server, p1001Client);
    assertEquals(2, p1001Server.sizeVisiteds());

    Address a1Removed = p1001Server.removeVisited(0);
    assertEquals(1, p1001Server.sizeVisiteds());
    assertEquals(a1.getGemId(), a1Removed.getGemId());
    assertEquals(0, a1.sizeVisitors());

    Log.info("Checking that the removal is synced to the client", "PersonSyncTest");
    clientContext.activate();
    assertEquals(1, p1001Client.sizeVisiteds());
    assertNotEquals(a1Removed.getGemId(), p1001Client.getVisited(0).getGemId());

    Log.info("Checking the second element is removable in the Server", "PersonSyncTest");
    serverContext.activate();
    Address a2Removed = p1001Server.removeVisited(0);
    assertEquals(0, p1001Server.sizeVisiteds());
    assertEquals(a2.getGemId(), a2Removed.getGemId());
    assertEquals(0, a2.sizeVisitors());

    Log.info("Checking that the removal is synced to the client", "PersonSyncTest");
    clientContext.activate();
    assertEquals(0, p1001Client.sizeVisiteds());
  }

  @Test
  public void testVisitedRemovalFromClient(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(200);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Building Person with visited Addresses in Client", "PersonSyncTest");
    clientContext.activate();

    Address a1Client = FlensburgManager.addressBuilder().city("Aachen").build().get();
    Address a2Client = FlensburgManager.addressBuilder().city("Flensburg").build().get();

    Log.info("Building Person", "PersonSyncTest");
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    Person p1001Client = FlensburgManager.personBuilder()
        .age(20)
        .nickname("Test")
        .mainResidence(a1Client)
        .visitedAdd(a1Client)
        .visitedAdd(a2Client)
        .build().get();

    Log.info("Checking it is removable in the Client", "PersonSyncTest");
    Address a1Removed = p1001Client.removeVisited(0);
    assertEquals(1, p1001Client.sizeVisiteds());
    assertEquals(a1Client.getGemId(), a1Removed.getGemId());
    assertEquals(0, a1Client.sizeVisitors());

    Log.info("Checking that the removal is synced to the server", "PersonSyncTest");
    serverContext.activate();
    Person p1001Server = FlensburgManager.getPersonSure(1001L);
    assertEquals(1, p1001Server.sizeVisiteds());
    assertNotEquals(a1Removed.getGemId(), p1001Server.getVisited(0).getGemId());

    Log.info("Checking the second element is removable in the Client", "PersonSyncTest");
    clientContext.activate();
    Address a2Removed = p1001Client.removeVisited(0);
    assertEquals(0, p1001Client.sizeVisiteds());
    assertEquals(a2Client.getGemId(), a2Removed.getGemId());
    assertEquals(0, a2Client.sizeVisitors());

    Log.info("Checking that the removal is synced to the Server", "PersonSyncTest");
    serverContext.activate();
    assertEquals(0, p1001Server.sizeVisiteds());

    Address a1Server = FlensburgManager.getAddressSure(a1Client.getGemId());
    Address a2Server = FlensburgManager.getAddressSure(a2Client.getGemId());
    assertEquals(0, a1Server.sizeVisitors());
    assertEquals(0, a2Server.sizeVisitors());
  }

  @Test
  public void testVisitedRemovalCrossClients(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(200);
    FlensburgTestContext clientContext1 = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();
    FlensburgTestContext clientContext2 = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Building Person with visited Addresses in Client 1", "PersonSyncTest");
    clientContext1.activate();

    Address a1Client1 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    Address a2Client1 = FlensburgManager.addressBuilder().city("Flensburg").build().get();

    Log.info("Building Person", "PersonSyncTest");
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    Person p1001Client1 = FlensburgManager.personBuilder()
        .age(20)
        .nickname("Test")
        .mainResidence(a1Client1)
        .visitedAdd(a1Client1)
        .visitedAdd(a2Client1)
        .build().get();

    Log.info("Checking it is removable in Client 2", "PersonSyncTest");
    clientContext2.activate();
    Person p1001Client2 = FlensburgManager.getPersonSure(1001L);
    Address a1Client2 = FlensburgManager.getAddressSure(a1Client1.getGemId());
    Address a2Client2 = FlensburgManager.getAddressSure(a2Client1.getGemId());

    Address a1Removed = p1001Client2.removeVisited(0);
    assertEquals(1, p1001Client2.sizeVisiteds());
    assertEquals(a1Client2.getGemId(), a1Removed.getGemId());
    assertEquals(0, a1Client2.sizeVisitors());

    Log.info("Checking that the removal is synced to the server", "PersonSyncTest");
    serverContext.activate();
    Person p1001Server = FlensburgManager.getPersonSure(1001L);
    assertEquals(1, p1001Server.sizeVisiteds());
    assertNotEquals(a1Removed.getGemId(), p1001Server.getVisited(0).getGemId());

    Log.info("Checking that the removal is synced to Client 1", "PersonSyncTest");
    clientContext1.activate();
    assertEquals(1, p1001Client1.sizeVisiteds());
    assertNotEquals(a1Removed.getGemId(), p1001Client1.getVisited(0).getGemId());

    Log.info("Checking the second element is removable in Client 1", "PersonSyncTest");
    clientContext1.activate();
    Address a2Removed = p1001Client1.removeVisited(0);
    assertEquals(0, p1001Client1.sizeVisiteds());
    assertEquals(a2Client1.getGemId(), a2Removed.getGemId());
    assertEquals(0, a2Client1.sizeVisitors());

    Log.info("Checking that the removal is synced to the Server", "PersonSyncTest");
    serverContext.activate();
    assertEquals(0, p1001Server.sizeVisiteds());
    Address a1Server = FlensburgManager.getAddressSure(a1Client1.getGemId());
    Address a2Server = FlensburgManager.getAddressSure(a2Client1.getGemId());
    assertEquals(0, a1Server.sizeVisitors());
    assertEquals(0, a2Server.sizeVisitors());

    Log.info("Checking that the removal is synced to Client 2", "PersonSyncTest");
    clientContext2.activate();
    assertEquals(0, p1001Client2.sizeVisiteds());

    assertEquals(0, a1Client2.sizeVisitors());
    assertEquals(0, a2Client2.sizeVisitors());
  }


  @Test
  public void testVisitedSetFromServer(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(200);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();

    Log.info("Building Person with visited Addresses in Client", "PersonSyncTest");
    clientContext.activate();

    Address a1 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    Address a2 = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Address a3 = FlensburgManager.addressBuilder().city("Jülich").build().get();

    Log.info("Building Person", "PersonSyncTest");
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    Person p1001Client = FlensburgManager.personBuilder()
        .age(20)
        .nickname("Test")
        .mainResidence(a1)
        .visited(Set.of(a1, a2, a3))
        .build().get();

    Log.info("Check setVisited from Server", "PersonSyncTest");
    serverContext.activate();
    Address a4 = FlensburgManager.addressBuilder().city("Braunschweig").build().get();
    Person p1001Server = FlensburgManager.getPersonSure(1001L);
    p1001Server.addVisited(1, a4);

    assertEquals(1, a4.sizeVisitors());
    assertEquals(p1001Server.getGemId(), a4._internal_getVisitorGemList().getID(0));

    Address a2Server = FlensburgManager.getAddressSure(a2.getGemId());
    assertEquals(1, a2Server.sizeVisitors());

    Log.info("Check the changes are synced to the Client", "PersonSyncTest");
    clientContext.activate();
    Address a4Client = FlensburgManager.getAddressSure(a4.getGemId());
    assertEquals(1, a4Client.sizeVisitors());
    assertEquals(p1001Server.getGemId(), a4Client._internal_getVisitorGemList().getID(0));
  }

  // TODO AHe: Continue here by testing more cases for Person#setVisited and Address#setVisitor
}
