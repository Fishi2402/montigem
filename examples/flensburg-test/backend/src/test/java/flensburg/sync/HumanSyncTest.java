/* (c) https://github.com/MontiCore/monticore */
package flensburg.sync;

// RTE-T: Testet Infrastruktur für die Synchronisation
// HWC-GT: Testet synchronisation der "generierten" Klasse Human

import de.se_rwth.commons.logging.Log;
import flensburg.infrastructure.FlensburgManagerClientImplBuilder;
import hardy.flensburg.FlensburgManager;
import hardy.flensburg.FlensburgTestContext;
import hardy.flensburg.Human;
import hardy.flensburg.HumanErrorCollector;
import hardy.flensburg.HumanErrorObserver;
import hardy.flensburg.test_infrastructure.FlensburgDBTestSetup;
import hardy.flensburg.test_infrastructure.FlensburgMockTestSetup;
import hardy.flensburg.test_infrastructure.FlensburgSQLDBTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import umlp.backendrte.test_infrastructure.TestSetup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface HumanSyncTest extends TestSetup<FlensburgTestContext> {

  class HumanSyncMockTest extends FlensburgMockTestSetup implements HumanSyncTest {
    public HumanSyncMockTest() {
      super(2);
    }
  }

  class HumanSyncDbTest extends FlensburgSQLDBTestSetup implements HumanSyncTest {
    public HumanSyncDbTest() {
      super(2);
    }
  }

  @BeforeEach
  default void clear(){
    Log.initDEBUG();
  }

  @Test
  default void testSyncBetweenTwoClients(){
    long hGemId = 1001L;

    // Create a human in client 1
    Log.info("Get Human in Client 1", "Test");
    getClients(0).activate();
    Human hClient1 = FlensburgManager.humanBuilderFromId(hGemId).age(20).build().get();

    // Check that 2nd client can load it
    Log.info("Get Human in Client 2", "Test");
    getClients(1).activate();
    Optional<Human> hClient2Opt = FlensburgManager.humanOpt(hGemId);
    assertTrue(hClient2Opt.isPresent());
    Human hClient2 = hClient2Opt.get();
    // Um die Synchronisierung zu testen, müssen hier zwei unterschiedliche Objekte existieren
    assertNotSame(hClient1, hClient2);
    assertEquals(20, hClient2.getAge());


    // Check that server can load it
    Log.info("Get Human in Server", "Test");
    getBackend().activate();
    Human hServer = FlensburgManager.getHumanSure(hGemId);
    assertNotNull(hServer);
    assertNotSame(hClient1, hServer);
    assertNotSame(hClient2, hServer);
    assertEquals(20, hServer.getAge());


    // Existierendes Objekt anpassen
    // von Client 1
    Log.info("Change Human in Client 1", "Test");
    getClients(0).activate();
    hClient1.setAge(30);
    assertEquals(30, hClient1.getAge());
    assertEquals(30, hClient2.getAge());
    assertEquals(30, hServer.getAge());

    // von Client 2
    Log.info("Change Human in Client 2", "Test");
    getClients(1).activate();
    hClient2.setAge(35);
    assertEquals(35, hClient1.getAge());
    assertEquals(35, hClient2.getAge());
    assertEquals(35, hServer.getAge());

    // vom Server
    Log.info("Change Human in Server", "Test");
    getBackend().activate();
    hServer.setAge(40);
    assertEquals(40, hClient1.getAge());
    assertEquals(40, hClient2.getAge());
    assertEquals(40, hServer.getAge());
  }

  @Disabled("Error concept will be replaced")
  @Test
  default void testInvalidAgeIsMirroredToClient(){
    // Create backend manager
    long gemId = 1001L;

    Human h = FlensburgManager.humanBuilderFromId(gemId).age(18).build().get();

    // Collect Server error messages
    List<String> serverErrorMsgs = new ArrayList<>();
    HumanErrorCollector.addErrorObserver(new HumanErrorObserver() {
      @Override
      public void notifyAgeInvalid(Human human, String msg) {
        serverErrorMsgs.add(msg);
      }
    });

    // Create client which prints different error messages
    HumanErrorCollector clientErrorCollector = new HumanErrorCollector(){
      @Override
      protected String _buildAgeInvalidMsg(Human human) {
        return "Client: Human age invalid " + human.getGemId();
      }
    };
    FlensburgTestContext clientContext1 = new FlensburgManagerClientImplBuilder()
        .backendContext(getBackend())
        .humanErrorCollector(clientErrorCollector)
        .build()
        .get();

    clientContext1.activate();
    List<String> clientErrorMsgs = new ArrayList<>();

    HumanErrorCollector.addErrorObserver(new HumanErrorObserver() {
      @Override
      public void notifyAgeInvalid(Human human, String msg) {
        clientErrorMsgs.add(msg);
      }
    });

    // Make sure Client 1 is subscribed to updates for gemId 1001
    Human h1001C1 = FlensburgManager.getHumanSure(gemId);

    // report age invalid at server
    getBackend().activate();
    HumanErrorCollector.reportAgeInvalid(h);

    assertEquals(1, serverErrorMsgs.size());
    assertEquals(1, clientErrorMsgs.size());

    String sError = serverErrorMsgs.get(0);
    String cError = clientErrorMsgs.get(0);

    assertEquals("Client: Human age invalid 1001", cError);

    assertNotEquals(sError, cError);

    // TODO: only sender of invalid command should be notified of this error?
  }
}
