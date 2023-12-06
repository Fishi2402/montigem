package dtcockpit;

/* (c) https://github.com/MontiCore/monticore */


import dtcockpit.test_infrastructure.DtcockpitDBTestSetup;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClearSoftValueHashMapTest extends DtcockpitDBTestSetup {
  public ClearSoftValueHashMapTest() {
    super(1);
  }

  @Test
  @Tag("LoadTest")
  public void testHashmap() {
    Room room0 = DtcockpitManagerImpl.roomBuilderFromId(0)
        .roomNumber(0).humidity(2.0)
        .temperature(2.0).atmosphericPressure(2.0)
        .motionDetected(false).level(2).build().get();
    for (int i = 1; i < 1000000; i++) {
      DtcockpitManagerImpl.roomBuilderFromId(i)
          .roomNumber(i).humidity(2.0)
          .temperature(2.0).atmosphericPressure(2.0)
          .motionDetected(false).level(2).build();
    }

    Map<Long,Room> knownRooms = DtcockpitManagerImpl.getKnownRooms();

    //check that Map got cleared
    assertTrue(knownRooms.size() < DtcockpitManagerImpl.getRoomList().size());
    //check that objects still in usage are not cleared
    assertTrue(DtcockpitManager.isLoadedRoom(0));
    //check that the longest existing object not in usage is among the cleared objects
    assertTrue(DtcockpitManager.isLoadedRoom(1));
    //check that
    assertEquals(room0, DtcockpitManager.getRoomSure(0));
    //check that Room cleared from map is still accessible through database
    assertEquals(1, DtcockpitManager.getRoomSure(1).getRoomNumber());
  }
}
