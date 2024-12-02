/* (c) https://github.com/MontiCore/monticore */
package autotechagil;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;

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
    Service cameraService = addService("Camera", ServiceStatus.GREEN, 10.0);
    Service lidarService = addService("LIDAR Sensor", ServiceStatus.GREEN, 10.0);
    Service steeringService = addService("Steering", ServiceStatus.GREEN, 10.0);
  }

  protected Service addService(String serviceName, ServiceStatus initialStatus, double initialTemp) {
    return AutoTechAgilManager.serviceBuilder()
        .name(serviceName)
        .status(initialStatus)
        .temperature(initialTemp)
        .build()
        .get();
  }
}
