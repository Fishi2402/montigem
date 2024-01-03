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
        .xPos(0.0)
        .yPos(0.0)
        .zPos(0.0)
        .width(200.0)
        .length(300.2)
        .height(52.8)
        .weight(1556.5)
        .stateOfCharge(0.00)
        .health(70.00)
        .type(type)
        .description(Optional.of("Ganzes Pack"))
        .isDeconstructible(true)
        .build().get();

    Process process = DemoRecManager.processBuilder()
        .startedAt(LocalDate.now())
        .deconstructionOf(pack)
        .build().get();

    DemoRecComponent component = DemoRecManager.demoRecComponentBuilder()
        .xPos(0.5)
        .yPos(15.0)
        .zPos(21.2)
        .width(200.0)
        .length(150.2)
        .height(52.8)
        .weight(1056.5)
        .description(Optional.of("Deckel"))
        .isDeconstructible(true)
        .build().get();

    DemoRecComponent component2 = DemoRecManager.demoRecComponentBuilder()
        .xPos(1.0)
        .yPos(187.0)
        .zPos(25.2)
        .width(2123.0)
        .length(150.425)
        .height(52.234)
        .weight(19132.5)
        .description(Optional.of("Unterteil"))
        .isDeconstructible(false)
        .build().get();

    DemoRecComponent component3 = DemoRecManager.demoRecComponentBuilder()
        .xPos(2.0)
        .yPos(1.0)
        .zPos(2.2)
        .width(3.0)
        .length(0.425)
        .height(2.234)
        .weight(1.5)
        .isDeconstructible(false)
        .build().get();

    JoinConnection connection = DemoRecManager.joinConnectionBuilder()
        .state(78.5)
        .type(JoinConnectionType.SCREW)
        .build().get();
    connection.addConnects(component2);
    connection.addConnects(component3);

    component.addComposedOf(connection);

    pack.addComposedOf(connection);

    EndEffector endEffector = DemoRecManager.endEffectorBuilder()
        .name("Screwdriver")
        .build().get();

    ProcessStep step = DemoRecManager.processStepBuilder()
        .type(ProcessStepType.DECONSTRUCTION)
        .uses(endEffector)
        .deconstructs(connection)
        .timeConsumption(786l)
        .index(0)
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
