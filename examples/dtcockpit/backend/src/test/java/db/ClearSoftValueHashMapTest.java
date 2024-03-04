package db;

/* (c) https://github.com/MontiCore/monticore */


import dtcockpit.DtcockpitManager;
import dtcockpit.DtcockpitManagerImpl;
import dtcockpit.Room;
import dtcockpit.test_infrastructure.DtcockpitDBTestSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import umlp.backendrte.common.DatabaseAccessManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClearSoftValueHashMapTest extends DtcockpitDBTestSetup {
  public ClearSoftValueHashMapTest() {
    super(1);
  }

  @Test
  @Tag("SlowTest")
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
    ResultSet rooms = DatabaseAccessManager.executeQuery("SELECT COUNT(*) FROM \"room\"").get();
    try {
      rooms.next();
      assertTrue(knownRooms.size() < rooms.getInt(1));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    //check that objects still in usage are not cleared
    Assertions.assertTrue(DtcockpitManager.isLoadedRoom(0));
    //check that the longest existing object not in usage is among the cleared objects
    assertFalse(DtcockpitManager.isLoadedRoom(1));
    //check that
    assertEquals(room0, DtcockpitManager.getRoomSure(0));
    //check that Room cleared from map is still accessible through database
    assertEquals(1, DtcockpitManager.getRoomSure(1).getRoomNumber());
  }
}
