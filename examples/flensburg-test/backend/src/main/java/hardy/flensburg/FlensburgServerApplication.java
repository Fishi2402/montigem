/* (c) https://github.com/MontiCore/monticore */
package hardy.flensburg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import java.io.IOException;

@SpringBootApplication(
    // We have our own data management
    exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages = {"umlp.backendrte.service.websocket"}
)
public class FlensburgServerApplication extends FlensburgServerApplicationTOP {

  public static void main(String[] args) {
    SpringApplication.run(FlensburgServerApplication.class, args);
  }

  @Override
  public void init() throws IOException {
    super.init();
    FlensburgDummies.getInstance().inventComplex();
  }

}
