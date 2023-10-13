package dtcockpit;

import de.se_rwth.commons.logging.Log;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@SpringBootApplication(
    exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages = {"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest", "dtcockpit.service", "security"}
)
public class DtcockpitServerApplication extends dtcockpit.DtcockpitServerApplicationTOP {

  public static void main(String[] args) {
    SpringApplication.run(DtcockpitServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    // Only create DummyData if database is empty
    if (DtcockpitManager.getUserList().isEmpty()) { // Is there a better way to check if dummy-data was already created?
      Log.println("Creating Dummy Data");
      DtcockpitManager.userBuilder()
          .username("MyUser")
          .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("MyPasswort"))
          .registrationDate(LocalDateTime.now())
          .email("unknown")
          .role(DtcockpitRole.ADMIN)
          .build();
    }
    else {
      Log.println("_NOT_ Creating New Dummy Data. Data is already present.");
    }

    try {
      Server webServer = Server.createWebServer("-web", "-webAllowOthers").start();
      Log.info(String.format("H2 Web Server started at: http://localhost:%s", webServer.getPort()), DtcockpitServerApplication.class.getName());
    }
    catch (SQLException e) {
      throw new IOException(e);
    }
  }

}
