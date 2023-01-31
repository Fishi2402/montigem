/* (c) https://github.com/MontiCore/monticore */
package log_mc_jar;

import com.google.common.io.Resources;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import umlp.rte.DatabaseAccessManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Objects;


@SpringBootApplication(
    // We have our own data management
    exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class}
)
public class StatServerApplication {
  public static void main(String[] args) throws IOException {
    SpringApplication.run(StatServerApplication.class, args);
    Log_MC_JARManagerImpl.init();
    String schema = Resources.toString(Objects.requireNonNull(Paths.get("examples","stat","target", "generated-sources",
        "monticore","sourcecode","Log_MC_JAR.sql").toUri().toURL()), StandardCharsets.UTF_8);
    DatabaseAccessManager.initSchema(schema);
  }
}
