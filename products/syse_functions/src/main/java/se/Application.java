package se;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import functionsstructure.FunctionsStructureManagerImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import umlp.backendrte.common.DatabaseAccessManager;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

// TODO: can we generate this?
@SpringBootApplication(
    // We have our own data management
    exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages={"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest"}
)
//@AutoConfiguration
public class Application {
  public static void main(String[] args) throws IOException {
    Log.enableFailQuick(false);
    Log.warn("I AM ALIVE!");

    FunctionsStructureManagerImpl.init();

    Optional<URL> optionalURL = Optional.ofNullable(Application.class.getResource("/FunctionsStructure.sql"));
    if(optionalURL.isEmpty()) {
      Log.error("0xAAAAAA Could not find schema for database.");
    }
    String schema = FileReaderWriter.readFromFile(optionalURL.get());
    Log.warn("Loading schema: " + schema);
    DatabaseAccessManager.initSchema(schema);
    SpringApplication.run(Application.class, args);
  }
}
