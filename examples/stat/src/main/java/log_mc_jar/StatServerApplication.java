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

    Log_MC_JARManagerImpl.init();
    Optional<URL> optionalURL = FileReaderWriter.getResource(StatServerApplication.class.getClassLoader(), "Log_MC_JAR.sql");
    if(optionalURL.isEmpty()) {
      Log.error("0xAAAAAA Could not find schema for database.");
    }
    String schema = FileReaderWriter.readFromFile(optionalURL.get());
    DatabaseAccessManager4SQL.initSchema(schema);

    // Allow External Access to H2 Database
    // Connect via "URL-Only" to "jdbc:h2:tcp://localhost/mem:umlpdb"
    // See database.properties for usename and password
    Server webServer = Server.createWebServer("-web", "-webAllowOthers").start();
    Log.info(String.format("H2 Web Server started at: http://localhost:%s", webServer.getPort()), StatServerApplication.class.getName());
    Server tcpServer = Server.createTcpServer("-tcp", "-tcpAllowOthers").start();
    Log.info(String.format("H2 Tcp Server started on port: %s", tcpServer.getPort()), StatServerApplication.class.getName());
    servers = Set.of(webServer, tcpServer); // Store servers to stop them later (required for HotReload)
  }

  @PreDestroy
  public void onExit() {
    try { DatabaseAccessManager.closeConnection();} catch (SQLException ignored) {}
    for (Server server : servers) {
      server.stop();
    }
  }
}
