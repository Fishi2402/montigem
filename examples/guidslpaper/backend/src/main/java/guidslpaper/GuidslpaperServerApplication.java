/* (c) https://github.com/MontiCore/monticore */
package guidslpaper;

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
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@SpringBootApplication(
    exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages = {"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest", "guidslpaper.service", "security"}
)
public class GuidslpaperServerApplication extends guidslpaper.GuidslpaperServerApplicationTOP {

  private static Set<Server> servers;

  public static void main(String[] args) {
    SpringApplication.run(GuidslpaperServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    // Only create DummyData if database is empty
    if (GuidslpaperManager.getUserList().isEmpty()) { // Is there a better way to check if dummy-data was already created?
      Log.println("Creating Dummy Data");
      GuidslpaperManager.userBuilder()
          .username("MyUser")
          .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("MyPasswort"))
          .registrationDate(LocalDate.now())
          .email("unknown")
          .role(Role.ADMIN)
          .build();
    }
    else {
      Log.println("_NOT_ Creating New Dummy Data. Data is already present.");
    }

    createDummyAccounts();
    createDummyPersons();

    try {
      Server webServer = Server.createWebServer("-web", "-webAllowOthers").start();
      Log.info(String.format("H2 Web Server started at: http://localhost:%s", webServer.getPort()), GuidslpaperServerApplication.class.getName());
      servers = Set.of(webServer);
    }
    catch (SQLException e) {
      throw new IOException(e);
    }

  }

  public void createDummyAccounts() {
    Konto k;
    k = GuidslpaperManager.kontoBuilder()
        .name("Haushaltskonto")
        .pspElement(Optional.of(""))
        .kontoart("")
        .kontotyp(Optional.of("94 (Haushaltskonto)"))
        .istPlanKonto(false)
        .budgetRahmenCent(100000L)
        .build()
        .get();

    k = GuidslpaperManager.kontoBuilder()
        .name("BMBF Stuttgart")
        .pspElement(Optional.of("172061234560003"))
        .kontoart("06")
        .kontotyp(Optional.of("BMBF"))
        .istPlanKonto(false)
        .budgetRahmenCent(8700L)
        .build()
        .get();

    k = GuidslpaperManager.kontoBuilder()
        .name("Haushaltskonto")
        .pspElement(Optional.of("131100108300000"))
        .kontoart("10")
        .kontotyp(Optional.of("94 (Haushaltskonto)"))
        .istPlanKonto(false)
        .budgetRahmenCent(100000L)
        .build()
        .get();

    k = GuidslpaperManager.kontoBuilder()
        .name("EU München")
        .pspElement(Optional.of("172101234560002"))
        .kontoart("10")
        .kontotyp(Optional.of("EU"))
        .istPlanKonto(false)
        .budgetRahmenCent(1900L)
        .build()
        .get();

    k = GuidslpaperManager.kontoBuilder()
        .name("AIF Berlin")
        .pspElement(Optional.of("172111234560005"))
        .kontoart("11")
        .kontotyp(Optional.of("AiF"))
        .istPlanKonto(false)
        .budgetRahmenCent(6100L)
        .build()
        .get();

    k = GuidslpaperManager.kontoBuilder()
        .name("HSPIII Stuttgart")
        .pspElement(Optional.of("172141234560007"))
        .kontoart("14")
        .kontotyp(Optional.of("HSPIII"))
        .istPlanKonto(false)
        .budgetRahmenCent(9905L)
        .build()
        .get();

    k = GuidslpaperManager.kontoBuilder()
        .name("FuE München")
        .pspElement(Optional.of("172651234560006"))
        .kontoart("65")
        .kontotyp(Optional.of("Sonderfonds"))
        .istPlanKonto(false)
        .budgetRahmenCent(4450L)
        .build()
        .get();
  }

  public void createDummyPersons() {
    Person p;
    p = GuidslpaperManager.personBuilder()
        .personalnummer(Optional.of("1234561"))
        .kuerzel("MUSMI")
        .nachname("Musterfrau")
        .vorname("Mia")
        .beschBeginn(Optional.of(LocalDate.of(2018, 1,1)))
        .beschEnde(Optional.of(LocalDate.of(2023, 12,31)))
        .build()
        .get();

    p = GuidslpaperManager.personBuilder()
        .personalnummer(Optional.of("1234"))
        .kuerzel("MUSMA")
        .nachname("Mustermann")
        .vorname("Max")
        .beschBeginn(Optional.of(LocalDate.of(2017, 5,15)))
        .beschEnde(Optional.of(LocalDate.of(2024, 12,31)))
        .build()
        .get();

    p = GuidslpaperManager.personBuilder()
        .personalnummer(Optional.of("1234"))
        .kuerzel("ENTOH")
        .nachname("Entgeltgruppe")
        .vorname("Ohne")
        .beschBeginn(Optional.of(LocalDate.of(2017, 5,15)))
        .beschEnde(Optional.of(LocalDate.of(2024, 12,31)))
        .build()
        .get();

    /*
    p = GuidslpaperManager.personBuilder()
        .personalnummer(Optional.of("62345"))
        .kuerzel("MUSMO")
        .nachname("Musterperson")
        .vorname("Mo")
        .beschBeginn(Optional.of(LocalDate.of(2018, 1,1)))
        .beschEnde(Optional.of(LocalDate.of(2025, 12,31)))
        .build()
        .get();

    p = GuidslpaperManager.personBuilder()
        .personalnummer(Optional.of("6382457"))
        .kuerzel("SCHPA")
        .nachname("Schmitz")
        .vorname("Paul")
        .beschBeginn(Optional.of(LocalDate.of(2017, 1,1)))
        .beschEnde(Optional.of(LocalDate.of(2023, 12,31)))
        .build()
        .get();

    p = GuidslpaperManager.personBuilder()
        .personalnummer(Optional.of("1234562"))
        .kuerzel("HAAFI")
        .nachname("Haas")
        .vorname("Finn")
        .beschBeginn(Optional.empty())
        .beschEnde(Optional.empty())
        .build()
        .get();

    p = GuidslpaperManager.personBuilder()
        .personalnummer(Optional.of("12346"))
        .kuerzel("BARFO")
        .nachname("Bar")
        .vorname("Foo")
        .beschBeginn(Optional.empty())
        .beschEnde(Optional.empty())
        .build()
        .get();
    /**/
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
