package stanley.sync;

// RTE-T: Testet Infrastruktur für die Synchronisation
// HWC-GT: Testet synchronisation der "generierten" Klasse Human

import de.se_rwth.commons.logging.Log;
import hardy.FlensburgManager;
import hardy.FlensburgMock;
import hardy.Human;
import org.junit.jupiter.api.Test;
import stanley.FlensburgManagerClientImpl;
import stanley.FlensburgTestContext;
import umlp.command.CommandManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HumanSyncTest {

  @Test
  public void testSyncBetweenTwoClients(){
    Log.initDEBUG();

    // Create backend manager
    FlensburgTestContext backendContext = new FlensburgTestContext(FlensburgMock.init(), CommandManager.getInstance());
    long hGemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(hGemId);

    // Create clients
    FlensburgTestContext clientContext1 = FlensburgManagerClientImpl.init(backendContext);
    FlensburgTestContext clientContext2 = FlensburgManagerClientImpl.init(backendContext);

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
}
