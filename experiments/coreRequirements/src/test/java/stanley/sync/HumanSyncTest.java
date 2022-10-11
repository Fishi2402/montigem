package stanley.sync;

// RTE-T: Testet Infrastruktur für die Synchronisation
// HWC-GT: Testet synchronisation der "generierten" Klasse Human

import de.se_rwth.commons.logging.Log;
import hardy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import infrastructure.command.FlensburgManagerClientImplBuilder;
import stanley.FlensburgTestContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HumanSyncTest {

  @BeforeEach
  public void clear(){
    FlensburgTestContext.clear();
  }

  @Test
  public void testSyncBetweenTwoClients(){
    Log.initDEBUG();

    // Create backend
    FlensburgMock.init();
    FlensburgTestContext backendContext = FlensburgTestContext.fromCurrentContext();
    long hGemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(hGemId);

    // Create clients
    FlensburgTestContext clientContext1 = new FlensburgManagerClientImplBuilder().backendContext(backendContext).build().get();
    FlensburgTestContext clientContext2 = new FlensburgManagerClientImplBuilder().backendContext(backendContext).build().get();

    // Create a human in client 1
    Log.info("Get Human in Client 1", "Test");
    clientContext1.activate();
    Human hClient1 = FlensburgManager.humanBuilder().age(20).build().get();


    // Check that 2nd client can load it
    Log.info("Get Human in Client 2", "Test");
    clientContext2.activate();
    Optional<Human> hClient2Opt = FlensburgManager.humanOpt(hGemId);
    assertTrue(hClient2Opt.isPresent());
    Human hClient2 = hClient2Opt.get();
    // Um die Synchronisierung zu testen, müssen hier zwei unterschiedliche Objekte existieren
    assertNotSame(hClient1, hClient2);
    assertEquals(20, hClient2.getAge());


    // Check that server can load it
    Log.info("Get Human in Server", "Test");
    backendContext.activate();
    Human hServer = FlensburgManager.getHumanSure(hGemId);
    assertNotNull(hServer);
    assertNotSame(hClient1, hServer);
    assertNotSame(hClient2, hServer);
    assertEquals(20, hServer.getAge());


    // Existierendes Objekt anpassen
    // von Client 1
    Log.info("Change Human in Client 1", "Test");
    clientContext1.activate();
    hClient1.setAge(30);
    assertEquals(30, hClient1.getAge());
    assertEquals(30, hClient2.getAge());
    assertEquals(30, hServer.getAge());

    // von Client 2
    Log.info("Change Human in Client 2", "Test");
    clientContext2.activate();
    hClient2.setAge(35);
    assertEquals(35, hClient1.getAge());
    assertEquals(35, hClient2.getAge());
    assertEquals(35, hServer.getAge());

    // vom Server
    Log.info("Change Human in Server", "Test");
    backendContext.activate();
    hServer.setAge(40);
    assertEquals(40, hClient1.getAge());
    assertEquals(40, hClient2.getAge());
    assertEquals(40, hServer.getAge());
  }

  @Test
  public void testInvalidAgeIsMirroredToClient(){
    Log.initDEBUG();

    // Create backend manager
    FlensburgMock.init();
    FlensburgTestContext backendContext = FlensburgTestContext.fromCurrentContext();
    long gemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(gemId);

    Human h = FlensburgManager.humanBuilder().age(18).build().get();

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
        .backendContext(backendContext)
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
    backendContext.activate();
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
