package db;

/* (c) https://github.com/MontiCore/monticore */


import dtcockpit.DtcockpitManagerClientImpl;
import dtcockpit.DtcockpitManagerNoRoomDBImpl;
import dtcockpit.DtcockpitTestContext;
import dtcockpit.Room;
import dtcockpit.test_infrastructure.DtcockpitDBTestSetup;
import dtcockpit.test_infrastructure.DtcockpitSQLDBTestSetup;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import umlp.backendrte.command.CommandDirectCommunication;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.TestClientCommandManager;
import umlp.backendrte.common.DatabaseAccessManager;
import umlp.backendrte.common.DatabaseAccessManager4SQL;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ClearSoftValueHashMapLoadTest extends DtcockpitSQLDBTestSetup {
  public ClearSoftValueHashMapLoadTest() {
    super(1);
  }


  @Override
  public  void _init ()
      throws Exception {
    DatabaseAccessManager4SQL.init();
    DtcockpitTestContext.clear();
    DtcockpitManagerNoRoomDBImpl.init();
    DatabaseAccessManager.executeStatement("DROP ALL OBJECTS");
    String schema = IOUtils.resourceToString("/Dtcockpit.sql", StandardCharsets.UTF_8);
    assert DatabaseAccessManager4SQL.initSchema(schema);
    backend = DtcockpitTestContext.fromCurrentContext();

    for (int i = 0; i < getNumberOfClients(); i++) {
      DtcockpitTestContext.clear();
      DtcockpitManagerNoRoomDBImpl.initMe(new DtcockpitManagerClientImpl());
      CommandDirectCommunication communication = new CommandDirectCommunication(backend);
      TestClientCommandManager clientCommandManager = new TestClientCommandManager(communication);
      CommandManager.initMe(clientCommandManager);
      var client = DtcockpitTestContext.fromCurrentContext();
      communication.setClientContext(client);
      clients.add(client);
    }
    backend.activate();
  }

  @Test
  @Tag("SlowTest")
  public void testHashmap() {
    int rooms = 200000000;
    Room room0 = DtcockpitManagerNoRoomDBImpl.roomBuilderFromId(0)
        .roomNumber(0).humidity(2.0)
        .temperature(2.0).atmosphericPressure(2.0)
        .motionDetected(false).level(2).build().get();
    for (int i = 1; i < rooms; i++) {
      DtcockpitManagerNoRoomDBImpl.roomBuilderFromId(i)
          .roomNumber(i).humidity(2.0)
          .temperature(2.0).atmosphericPressure(2.0)
          .motionDetected(false).level(2).build();
    }

    Map<Long,Room> knownRooms = DtcockpitManagerNoRoomDBImpl.getKnownRooms();

    assertTrue(knownRooms.size() < rooms);
    //check that objects still in usage are not cleared
    assertTrue(DtcockpitManagerNoRoomDBImpl.isLoadedRoom(0));
    //check that
    assertEquals(room0, DtcockpitManagerNoRoomDBImpl.getRoomSure(0));
    //check that the longest existing object not in usage is among the cleared objects
    assertFalse(DtcockpitManagerNoRoomDBImpl.isLoadedRoom(1));
  }
}
