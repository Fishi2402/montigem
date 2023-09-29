/* (c) https://github.com/MontiCore/monticore */
package flensburg.sync;

import hardy.flensburg.Car;
import hardy.flensburg.FlensburgManager;
import hardy.flensburg.test_infrastructure.FlensburgMockTestSetup;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CarSyncTest extends FlensburgMockTestSetup {

  public CarSyncTest() {
    super(1);
  }

  @Test
  public void testSetNameInServer(){
    Car c1 = FlensburgManager.carBuilder()
        .wheelLeft(true).build().get();

    getClients(0).activate();
    Car c1Client = FlensburgManager.getCarSure(c1.getGemId());
    assertNotNull(c1Client);
    assertEquals(0, c1Client.getNamesOfAllPartsList().size());

    getBackend().activate();
    c1.addNamesOfAllParts(0, "Foo");
    assertEquals(1, c1.getNamesOfAllPartsList().size());
    assertEquals(1, c1Client.getNamesOfAllPartsList().size());
  }

  @Test
  public void testSetNameInClient(){
    getClients(0).activate();
    Car c1 = FlensburgManager.carBuilder()
        .wheelLeft(true).build().get();

    getBackend().activate();
    Car c1Server = FlensburgManager.getCarSure(c1.getGemId());
    assertNotNull(c1Server);
    assertEquals(0, c1Server.getNamesOfAllPartsList().size());

    getClients(0).activate();
    c1.setWheelLeft(false);
    assertFalse(c1.isWheelLeft());
    assertFalse(c1Server.isWheelLeft());

    c1.addNamesOfAllParts(0, "Foo");
    assertEquals(1, c1.getNamesOfAllPartsList().size());
    assertEquals(1, c1Server.getNamesOfAllPartsList().size());
  }

  @Test
  public void testRemoveNameInServer(){
    Car c1 = FlensburgManager.carBuilder()
        .wheelLeft(true).namesOfAllParts(Lists.newArrayList("Foo")).build().get();

    getClients(0).activate();
    Car c1Client = FlensburgManager.getCarSure(c1.getGemId());
    assertNotNull(c1Client);
    assertEquals(1, c1Client.getNamesOfAllPartsList().size());

    getBackend().activate();
    c1.removeNamesOfAllParts(0);
    assertEquals(0, c1.getNamesOfAllPartsList().size());
    assertEquals(0, c1Client.getNamesOfAllPartsList().size());
  }

  @Test
  public void testRemoveNameInClient(){
    Car c1 = FlensburgManager.carBuilder()
        .wheelLeft(true).namesOfAllParts(Lists.newArrayList("Foo")).build().get();

    getClients(0).activate();
    Car c1Client = FlensburgManager.getCarSure(c1.getGemId());
    assertNotNull(c1Client);
    assertEquals(1, c1Client.getNamesOfAllPartsList().size());

    c1Client.removeNamesOfAllParts(0);
    assertEquals(0, c1.getNamesOfAllPartsList().size());
    assertEquals(0, c1Client.getNamesOfAllPartsList().size());
  }
}
