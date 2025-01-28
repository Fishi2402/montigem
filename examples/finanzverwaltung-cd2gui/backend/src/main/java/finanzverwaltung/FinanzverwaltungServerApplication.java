/* (c) https://github.com/MontiCore/monticore */
package finanzverwaltung;

import de.se_rwth.commons.logging.Log;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import umlp.backendrte.common.DatabaseAccessManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Set;

@SpringBootApplication(
    exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages = {"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest", "finanzverwaltung.service", "security"}
)
public class FinanzverwaltungServerApplication extends finanzverwaltung.FinanzverwaltungServerApplicationTOP {

  private static Set<Server> servers;

  public static void main(String[] args) {
    SpringApplication.run(FinanzverwaltungServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    // Only create DummyData if database is empty
    if (FinanzverwaltungManager.getUserList().isEmpty()) { // Is there a better way to check if dummy-data was already created?
      Log.println("Creating Dummy Data");
      FinanzverwaltungManager.userBuilder()
          .username("MyUser")
          .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("MyPasswort"))
          .registrationDate(LocalDateTime.now())
          .email("unknown")
          .role(Role.ADMIN)
          .build();
    }
    else {
      Log.println("_NOT_ Creating New Dummy Data. Data is already present.");
    }

    try {
      Server webServer = Server.createWebServer("-web", "-webAllowOthers").start();
      Log.info(String.format("H2 Web Server started at: http://localhost:%s", webServer.getPort()), FinanzverwaltungServerApplication.class.getName());
      servers = Set.of(webServer);
    }
    catch (SQLException e) {
      throw new IOException(e);
    }

  }

  // For HotReload helpful. Otherwise the h2-webserverport is used when reloading
  // ToDo: How to preserve the most data across reload? e.g. when dataschema is the same
  @PreDestroy
  public void onExit() {
    Log.warn("PreDestroy: Closing DB Connection");
    try {
      DatabaseAccessManager.closeConnection();
    }
    catch (SQLException ignored) {
    }
    for (Server server : servers) {
      server.stop();
    }
  }

}
