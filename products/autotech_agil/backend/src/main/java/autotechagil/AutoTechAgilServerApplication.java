/* (c) https://github.com/MontiCore/monticore */
package autotechagil;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;

@SpringBootApplication(
    exclude={ DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages={"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest", "service"}
)

public class AutoTechAgilServerApplication extends AutoTechAgilServerApplicationTOP {

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    this.createDummyData();
  }

  public void createDummyData() {
    Service cameraService = createService("Camera");
    Service lidarService = createService("LIDAR Sensor");
    Service steeringService = createService("Steering");

    Port portA = createPort("Port A");
    Port portB = createPort("Port B");
    Port portC = createPort("Port C");
    Port portD = createPort("Port D");
    Port portE = createPort("Port E");
    Port portF = createPort("Port F");
    Port portG = createPort("Port G");
    Port portWheelFLIn = createPort("Wheel Front Left");
    Port portWheelFRIn = createPort("Wheel Front Right");
    Port portWheelBLIn = createPort("Wheel Back Left");
    Port portWheelBRIn = createPort("Wheel Back Right");
    Port portWheelFLOut = createPort("Wheel Front Left");
    Port portWheelFROut = createPort("Wheel Front Right");
    Port portWheelBLOut = createPort("Wheel Back Left");
    Port portWheelBROut = createPort("Wheel Back Right");

    cameraService.addAllIns(Arrays.asList(portA, portB));
    cameraService.addOut(portC);

    lidarService.addIn(portD);
    lidarService.addAllOuts(Arrays.asList(portE, portF, portG));

    steeringService.addAllIns(
        Arrays.asList(portWheelFLIn, portWheelFRIn, portWheelBLIn, portWheelBRIn)
    );
    steeringService.addAllOuts(
        Arrays.asList(portWheelFLOut, portWheelFROut, portWheelBLOut, portWheelBROut)
    );
  }

  protected Service createService(String functionName) {
    return AutoTechAgilManager.serviceBuilder()
        .name(functionName)
        .build()
        .get();
  }

  protected Port createPort(String portName) {
    return AutoTechAgilManager.portBuilder()
        .name(portName)
        .build()
        .get();
  }
}
