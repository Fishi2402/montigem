/* (c) https://github.com/MontiCore/monticore */
package hardy;

import com.google.common.io.Resources;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import umlp.backendrte.common.DatabaseAccessManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


@SpringBootApplication(
    // We have our own data management
    exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class}
)
public class UMLPApplication {
  public static void main(String[] args) throws IOException {
    SpringApplication.run(UMLPApplication.class, args);
    FlensburgMock.init();
    FlensburgManagerImpl.init();
    String schema = Resources.toString(Objects.requireNonNull(UMLPApplication.class.getResource("/Flensburg.sql")), StandardCharsets.UTF_8);
    if (DatabaseAccessManager.initSchema(schema)) {
      (new FlensburgDummies()).inventDummiesOne();
    }
  }
}
