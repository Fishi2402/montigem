package demorec;

import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import org.h2.tools.Server;
import umlp.backendrte.common.DatabaseAccessManager;
import umlp.backendrte.common.DatabaseAccessManager4SQL;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DemoRecServerApplication extends DemoRecServerApplicationTOP {

  public static String blobStorageLocation = "";

  @javax.annotation.PostConstruct
  @Override
  public  void init () throws IOException
  {

    Log.enableFailQuick(false);
    Log.info("I AM ALIVE!", this.getClass().getName());

    DatabaseAccessManager4SQL.init();
    DemoRecManagerImpl.init();

    Optional<URL> optionalURL = Optional.ofNullable(DemoRecServerApplication.class.getResource("/DemoRec.sql"));
    if(optionalURL.isEmpty()) {
      Log.error("0xAAAAAA Could not find schema for database.");
    }
    String schema = FileReaderWriter.readFromFile(optionalURL.get());
    Log.info("Loading schema: " + schema, this.getClass().getName());
    DatabaseAccessManager4SQL.initSchema(schema);


    setBlobStorageLocation();

    BatteryPackType type2 = DemoRecManager.batteryPackTypeBuilder()
        .capacity(100.00)
        .bmsLocation("Motorraum")
        .carType("VW ID4")
        .build().get();

    BatteryPackType type = DemoRecManager.batteryPackTypeBuilder()
        .capacity(100.00)
        .bmsLocation("Motorraum")
        .carType("VW ID4")
        .build().get();

    BatteryPack pack = DemoRecManager.batteryPackBuilder()
        .stateOfCharge(0.00)
        .health(70.00)
        .photoLocation(List.of())
        .type(type)
        .build().get();

    Process process = DemoRecManager.processBuilder()
        .startedAt(LocalDate.now())
        .deconstructionOf(pack)
        .build().get();

    Composite component = DemoRecManager.compositeBuilder()
        .xPos(0.5)
        .yPos(15.0)
        .zPos(21.2)
        .width(200.0)
        .length(150.2)
        .height(52.8)
        .weight(1056.5)
        .isDesconstructible(true)
        .photoLocations(List.of())
        .threeDFileLocations(List.of())
        .build().get();
    pack.addConsistsOf(component);

    JoinConnection connection = DemoRecManager.joinConnectionBuilder()
        .state(78.5)
        .type(JoinConnectionType.SCREW)
        .build().get();
    connection.addDemoRecComponent(component);


    EndEffector endEffector = DemoRecManager.endEffectorBuilder()
        .name("Screwdriver")
        .build().get();

    ProcessStep step = DemoRecManager.processStepBuilder()
        .type(ProcessStepType.DECONSTRCUTION)
        .uses(endEffector)
        .deconstructs(connection)
        .timeconsumption(786l)
        .build().get();

    process.addConsistsOf(step);
  }

  public void setBlobStorageLocation() {
    if(System.getenv("FILE_LOCATION") != null) {
      String filePath = System.getenv("FILE_LOCATION");
      if(!filePath.endsWith("/")) {
        blobStorageLocation=filePath+"/";
      } else {
        blobStorageLocation=filePath;
      }
      Log.info("Set file location to: "+blobStorageLocation, this.getClass().getName());
    }
  }

  protected static void openWebServer() {
    try {
      Server.startWebServer(DatabaseAccessManager.getConnection());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
