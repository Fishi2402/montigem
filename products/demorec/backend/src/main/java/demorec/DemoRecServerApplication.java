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

    super.init();

    setBlobStorageLocation();

    loadDummyDataIfNotDeployed();
  }

  private void loadDummyDataIfNotDeployed() {
    if(System.getenv("deploy") == null  || !System.getenv("deploy").equalsIgnoreCase("true")) {
      BatteryPackType type = DemoRecManager.batteryPackTypeBuilder()
          .capacity(100.00)
          .voltage(750.00)
          .bmsLocation("Motorraum")
          .vin("000")
          .carBrand(Optional.of("VW"))
          .carModel(Optional.of("ID4"))
          .carYear(Optional.of("2017"))
          .build().get();

      BatteryPack pack = DemoRecManager.batteryPackBuilder()
          .name("Demontage gestartet am 24.01.29")
          .condition(State.OTHER)
          .conditionDescription(Optional.of("Unfall Pack"))
          .xPos(Optional.of(0.0))
          .yPos(Optional.of(0.0))
          .zPos(Optional.of(0.0))
          .isA(
              DemoRecManager.componentKindBuilder()
                  .name("VW Pack")
                  .oENumber(Optional.of("27347239"))
                  .humanDescription(Optional.of("Ganzes Pack"))
                  .width(Optional.of(200.0))
                  .length(Optional.of(300.2))
                  .height(Optional.of(52.8))
                  .weight(Optional.of(1556.5))
                  .build().get()
          )
          .stateOfCharge(0.00)
          .health(Optional.of(70.00))
          .type(type)
          .build().get();

      BatteryPackType type2 = DemoRecManager.batteryPackTypeBuilder()
          .capacity(200.00)
          .voltage(230.00)
          .bmsLocation("Motorraum")
          .vin("4652134")
          .carBrand(Optional.of("Renault"))
          .carModel(Optional.of("Zoe"))
          .carYear(Optional.of("2019"))
          .build().get();

      BatteryPack pack2 = DemoRecManager.batteryPackBuilder()
          .name("Zoe Pack")
          .condition(State.HEALTHY)
          .conditionDescription(Optional.empty())
          .xPos(Optional.of(1.0))
          .yPos(Optional.of(0.0))
          .zPos(Optional.of(2.0))
          .isA(
              DemoRecManager.componentKindBuilder()
                  .name("Zoe Pack")
                  .oENumber(Optional.of("5462138"))
                  .humanDescription(Optional.of("Zoe Pack"))
                  .width(Optional.of(230.0))
                  .length(Optional.of(800.2))
                  .height(Optional.of(25.8))
                  .weight(Optional.of(450.5))
                  .build().get()
          )
          .stateOfCharge(0.00)
          .health(Optional.of(98.00))
          .type(type2)
          .build().get();

      Process process = DemoRecManager.processBuilder()
          .startedAt(LocalDate.now())
          .deconstructionOf(pack)
          .build().get();

      Process process2 = DemoRecManager.processBuilder()
          .startedAt(LocalDate.now())
          .deconstructionOf(pack2)
          .build().get();

      DemoRecComponent component = DemoRecManager.demoRecComponentBuilder()
          .name("Deckel_1")
          .condition(State.HEALTHY)
          .xPos(Optional.of(0.5))
          .yPos(Optional.of(15.0))
          .zPos(Optional.of(21.2))
          .isA(
              DemoRecManager.componentKindBuilder()
                  .name("Deckel")
                  .oENumber(Optional.of("27347239"))
                  .humanDescription(Optional.of("Deckel"))
                  .width(Optional.of(200.0))
                  .length(Optional.of(150.2))
                  .height(Optional.of(52.8))
                  .weight(Optional.of(1056.5))
                  .build().get()
          )
          .build().get();

      DemoRecComponent component2 = DemoRecManager.demoRecComponentBuilder()
          .name("PackUnterteil_1")
          .condition(State.HEALTHY)
          .xPos(Optional.of(1.0))
          .yPos(Optional.of(187.0))
          .zPos(Optional.of(25.2))
          .isA(
              DemoRecManager.componentKindBuilder()
                  .name("PackUnterteil")
                  .oENumber(Optional.of("27347239"))
                  .humanDescription(Optional.of("Pack nach Deckelentfernung"))
                  .width(Optional.of(2123.0))
                  .length(Optional.of(150.425))
                  .height(Optional.of(52.234))
                  .weight(Optional.of(19132.5))
                  .build().get()
          )
          .build().get();

      pack.addAllComponents(component);
      pack.addAllComponents(component2);

      JoinConnectionKind kind = DemoRecManager.joinConnectionKindBuilder()
          .joinConnectionType(JoinConnectionType.SCREW)
          .connectsAdd(component)
          .connectsAdd(component2)
          .build().get();

      JoinConnection connection = DemoRecManager.joinConnectionBuilder()
          .condition(State.OTHER)
          .conditionDescription(Optional.of("Korrodiert"))
          .xPos(Optional.of(65.8))
          .yPos(Optional.of(5.1))
          .zPos(Optional.of(-89.5622))
          .joinConnectionKind(kind)
          .build().get();

      pack.addAllConnections(kind);

      EndEffector endEffector = DemoRecManager.endEffectorBuilder()
          .name("Screwdriver")
          .build().get();

      EndEffector endEffector2 = DemoRecManager.endEffectorBuilder()
          .name("Allen key")
          .build().get();

      ProcessStep step = DemoRecManager.processStepBuilder()
          .type(ProcessStepType.DECONSTRUCTION)
          .uses(endEffector)
          .deconstructs(connection)
          .timeConsumption(786.0)
          .index(0)
          .build().get();

      ProcessStep step2 = DemoRecManager.processStepBuilder()
          .type(ProcessStepType.DECONSTRUCTION)
          .uses(endEffector)
          .deconstructs(connection)
          .timeConsumption(23.0)
          .index(1)
          .build().get();

      process.addConsistsOf(step);
      process.addConsistsOf(step2);

      DemoRecManager.dimensionBuilder().name("Torque").unit("Nm").build().get();

      //openWebServer();
    }
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
      Server tcpServer = Server.createTcpServer("-tcp", "-tcpAllowOthers").start();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
