/* (c) https://github.com/MontiCore/monticore */
package de.rwth.se.se_lib;

import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import selib.SELibManagerImpl;
import umlp.backendrte.common.DatabaseAccessManager;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

// TODO: can we generate this?
@SpringBootApplication(
    // We have our own data management
    exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages={"umlp.backendrte.service.websocket"}
)
//@AutoConfiguration
public class SeLibServerApplication {
  public static void main(String[] args) throws IOException {
    Log.warn("I AM ALIVE!");
    SELibManagerImpl.init();

    Optional<URL> optionalURL = Optional.ofNullable(SeLibServerApplication.class.getResource("/SELib.sql"));
    if(optionalURL.isEmpty()) {
      Log.error("0xAAAAAA Could not find schema for database.");
    }
    String schema = FileReaderWriter.readFromFile(optionalURL.get());
    Log.warn("Loading schema: " + schema);
    DatabaseAccessManager.initSchema(schema);
    SpringApplication.run(SeLibServerApplication.class, args);
  }
}
