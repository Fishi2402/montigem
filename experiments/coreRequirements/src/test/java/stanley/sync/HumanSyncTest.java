package stanley.sync;

import hardy.FlensburgManager;
import hardy.FlensburgMock;
import hardy.Human;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import stanley.FlensburgManagerClientImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HumanSyncTest {

  @Disabled("TODO: implement (de)serialization of commands")
  @Test
  public void testSyncBetweenTwoClients(){
    // Create backend manager
    FlensburgMock.init();
    FlensburgManager backend = FlensburgManager.getSingleton();
    long hGemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(hGemId);

    // Create clients
    FlensburgManagerClientImpl fm1 = new FlensburgManagerClientImpl();
    FlensburgManagerClientImpl fm2 = new FlensburgManagerClientImpl();

    // Create a human in client 1
    fm1.use(backend);
    Human hClient1 = FlensburgManager.humanBuilder().age(20).build().get();

    // Check that 2nd client can load it
    fm2.use(backend);
    Optional<Human> hClient2Opt = FlensburgManager.humanOpt(hGemId);
    assertTrue(hClient2Opt.isPresent());
    Human hClient2 = hClient2Opt.get();

    // Um die Synchronisierung zu testen, müssen hier zwei seperate Objekte existieren
    assertNotSame(hClient1, hClient2);

    assertEquals(20, hClient2.getAge());

    // Existierendes Objekt anpassen
    // von Client 1
    fm1.use(backend);
    hClient1.setAge(30);
    assertEquals(30, hClient2.getAge());

    // von Client 2
    fm2.use(backend);
    hClient2.setAge(35);
    assertEquals(35, hClient1.getAge());
  }
}
