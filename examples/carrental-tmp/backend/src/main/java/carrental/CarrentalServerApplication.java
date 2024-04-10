/* (c) https://github.com/MontiCore/monticore */

package carrental;

import de.se_rwth.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;

@SpringBootApplication(
    // We have our own data management
    exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages = {"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest", "carrental.service"}
)
public class CarrentalServerApplication extends CarrentalServerApplicationTOP {

  public static void main(String[] args) {
    SpringApplication.run(CarrentalServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    if (CarrentalManager.getCarList().isEmpty()) {
      // Create initial data
      Optional<Car> newHerbie = CarrentalManager.carBuilder()
          .make("VW")
          .model("Käfer")
          .name("Herbie")
          .build();

      for (int i = 1; i < 10; i++)
        CarrentalManager.carBuilder()
            .make("Make" + i)
            .model("Model" + i)
            .name("Name" + i)
            .build();


    }

  }
}
