/* (c) https://github.com/MontiCore/monticore */
package log_mc_jar;

import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import umlp.backendrte.common.DatabaseAccessManager;
import umlp.backendrte.common.DatabaseAccessManager4Neo4j;
import umlp.backendrte.common.DatabaseAccessManager4SQL;

import javax.annotation.PreDestroy;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;


@SpringBootApplication(
    // We have our own data management
    exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class}
)
public class StatServerApplication {
  private static Set<Server> servers;
  public static void main(String[] args) throws SQLException {
    SpringApplication.run(StatServerApplication.class, args);

    DatabaseAccessManager4Neo4j.init();

    Log_MC_JARManagerImpl.init();
  }

  @PreDestroy
  public void onExit() {
    try { DatabaseAccessManager.closeConnection();} catch (SQLException ignored) {}
    for (Server server : servers) {
      server.stop();
    }
  }
}
