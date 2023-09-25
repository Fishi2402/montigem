/* (c) https://github.com/MontiCore/monticore */
package flensburg.sync;

import de.se_rwth.commons.logging.Log;
import flensburg.infrastructure.FlensburgManagerClientImplBuilder;
import hardy.flensburg.Car;
import hardy.flensburg.FlensburgManager;
import hardy.flensburg.FlensburgMock;
import hardy.flensburg.FlensburgTestContext;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CarSyncTest {
  @BeforeEach
  public void clear(){
    Log.initDEBUG();
    FlensburgTestContext.clear();
  }

  @Test
  public void testSetNameInServer(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    FlensburgTestContext clientContext1 = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    Car c1 = FlensburgManager.carBuilder()
        .wheelLeft(true).build().get();

    clientContext1.activate();
    Car c1Client = FlensburgManager.getCarSure(startGemId);
    assertNotNull(c1Client);
    assertEquals(0, c1Client.getNamesOfAllPartsList().size());

    serverContext.activate();
    c1.addNamesOfAllParts(0, "Foo");
    assertEquals(1, c1.getNamesOfAllPartsList().size());
    assertEquals(1, c1Client.getNamesOfAllPartsList().size());

  }

  @Test
  public void testSetNameInClient(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    FlensburgTestContext clientContext1 = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    clientContext1.activate();
    Car c1 = FlensburgManager.carBuilder()
        .wheelLeft(true).build().get();

    serverContext.activate();
    Car c1Server = FlensburgManager.getCarSure(startGemId);
    assertNotNull(c1Server);
    assertEquals(0, c1Server.getNamesOfAllPartsList().size());

    clientContext1.activate();
    c1.setWheelLeft(false);
    assertFalse(c1.isWheelLeft());
    assertFalse(c1Server.isWheelLeft());

    c1.addNamesOfAllParts(0, "Foo");
    assertEquals(1, c1.getNamesOfAllPartsList().size());
    assertEquals(1, c1Server.getNamesOfAllPartsList().size());

  }

  @Test
  public void testRemoveNameInServer(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    FlensburgTestContext clientContext1 = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptNextGemId(startGemId);
    Car c1 = FlensburgManager.carBuilder()
        .wheelLeft(true).namesOfAllParts(Lists.newArrayList("Foo")).build().get();

    clientContext1.activate();
    Car c1Client = FlensburgManager.getCarSure(startGemId);
    assertNotNull(c1Client);
    assertEquals(1, c1Client.getNamesOfAllPartsList().size());

    serverContext.activate();
    c1.removeNamesOfAllParts(0);
    assertEquals(0, c1.getNamesOfAllPartsList().size());
    assertEquals(0, c1Client.getNamesOfAllPartsList().size());

  }
}
