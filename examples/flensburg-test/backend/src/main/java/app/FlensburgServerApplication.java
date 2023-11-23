/* (c) https://github.com/MontiCore/monticore */
package app;

import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import hardy.flensburg.FlensburgDummies;
import hardy.flensburg.FlensburgManagerImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import umlp.backendrte.common.DatabaseAccessManager;
import umlp.backendrte.common.DatabaseAccessManager4SQL;

import java.net.URL;
import java.util.Optional;


@SpringBootApplication(
    // We have our own data management
    exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages = {"umlp.backendrte.service.websocket"}
)
public class FlensburgServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(FlensburgServerApplication.class, args);
    DatabaseAccessManager4SQL.init();
    FlensburgManagerImpl.init();

    Optional<URL> optionalURL = FileReaderWriter.getResource(FlensburgServerApplication.class.getClassLoader(), "Flensburg.sql");
    if (!optionalURL.isPresent()) {
      Log.error("0xAAAAAA Could not find schema for database.");
    }
    String schema = FileReaderWriter.readFromFile(optionalURL.get());
    Log.warn("Loading schema: " + schema);
    if (DatabaseAccessManager4SQL.initSchema(schema)) {
      FlensburgDummies.getInstance().inventComplex();
    }
  }
}
