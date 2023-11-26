package kitchencompanion;

import de.se_rwth.commons.logging.Log;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class KitchenCompanionServerApplication extends KitchenCompanionServerApplicationTOP {

  public static void main(String[] args) {
    SpringApplication.run(KitchenCompanionServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    // Only create DummyData if database is empty
    if (KitchenCompanionManager.getUserList().isEmpty()) { // Is there a better way to check if dummy-data was already created?
      Log.println("Creating Dummy Data");
      KitchenCompanionManager.userBuilder()
          .username("MyUser")
          .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("MyPasswort"))
          .email("unknown")
          .role(KitchenCompanionRole.ADMIN)
          .build();
    }
    else {
      Log.println("_NOT_ Creating New Dummy Data. Data is already present.");
    }


    try {
      Server webServer = Server.createWebServer("-web", "-webAllowOthers").start();
      Log.info(String.format("H2 Web Server started at: http://localhost:%s", webServer.getPort()), KitchenCompanionServerApplication.class.getName());
    }
    catch (SQLException e) {
      throw new IOException(e);
    }
  }

  public void createUsers() {
    if (KitchenCompanionManager.getUserList().isEmpty()) {
      Log.println("Creating Dummy Data");
      KitchenCompanionManager.userBuilder()
          .username("admin")
          .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("pwd"))
          .email("admin@email.de")
          .role(KitchenCompanionRole.ADMIN)
          .build();
      KitchenCompanionManager.userBuilder()
          .username("sek")
          .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("sek"))
          .email("sek@email.de")
          .role(KitchenCompanionRole.ACCOUNTANT)
          .build();

      // ordinary users
      KitchenCompanionManager.userBuilder()
          .username("Alice")
          .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Bob")
          .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("b"))
          .email("b@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Charlie")
          .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("c"))
          .email("c@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

    }
    else {
      Log.println("_NOT_ Creating New Dummy Data. Data is already present.");
    }
  }
}
