package kitchencompanion;

import de.se_rwth.commons.logging.Log;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
//import org.springframework.security.crypto.factory.PasswordEncoderFactories;

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
      createUsers();
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
//      KitchenCompanionManager.userBuilder()
//          .username("admin")
//          .password(("pwd"))
//          .email("admin@email.de")
//          .role(KitchenCompanionRole.ADMIN)
//          .build();
//      KitchenCompanionManager.userBuilder()
//          .username("sek")
//          .password( ("sek"))
//          .email("sek@email.de")
//          .role(KitchenCompanionRole.ACCOUNTANT)
//          .build();

      // lead users
      KitchenCompanionManager.userBuilder()
          .username("Bernhard Rumpe")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Manfred Nagl")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();


      // ordinary users
      KitchenCompanionManager.userBuilder()
          .username("Vincent Bertram")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Jonas Böcker")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Marita Breuer")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Constantin Buschhaus")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Joel Charles")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Juan Sebastian Diaz")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Florian Drux")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Lars Fischer")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Stefanie Fischer")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Arkadii Gerasimov")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Julius Gummersbach")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Sylvia Gunder")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Malte Heithoff")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Alexander Hellwig")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Steffen Hillemacher")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Anna Hühnerbein")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Nico Jansen")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Hendrik Kausch")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Alexander Lüpges")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Judith Michael")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Bogdan Naskow")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Lukas Netz")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Haron Nqiri")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Mathias Pfeiffer")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Deni Raco")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("David Schmalzing")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Marc Schmidt")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Max Stachon")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Felix Steinfurth")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Sebastian Stüber")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Samuel Thesing")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Galina Volkova")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Louis Wachtmeister")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

      KitchenCompanionManager.userBuilder()
          .username("Lucas Wollenhaupt")
          .password( ("a"))
          .email("a@se-rwth.de")
          .role(KitchenCompanionRole.USER)
          .build();

    }
    else {
      Log.println("_NOT_ Creating New Dummy Data. Data is already present.");
    }
  }
}
