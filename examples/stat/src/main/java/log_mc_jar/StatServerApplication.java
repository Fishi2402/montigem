/* (c) https://github.com/MontiCore/monticore */
package log_mc_jar;

import com.google.common.io.Resources;
import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import umlp.rte.DatabaseAccessManager;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;


@SpringBootApplication(
    // We have our own data management
    exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class}
)
public class StatServerApplication {
  public static void main(String[] args) throws IOException {
    SpringApplication.run(StatServerApplication.class, args);
    Log_MC_JARManagerImpl.init();
    Optional<URL> optionalURL = FileReaderWriter.getResource(StatServerApplication.class.getClassLoader(), "Log_MC_JAR.sql");
    if(optionalURL.isEmpty()) {
      Log.error("0xAAAAAA Could not find schema for database.");
    }
    String schema = FileReaderWriter.readFromFile(optionalURL.get());
    DatabaseAccessManager.initSchema(schema);
  }
}
