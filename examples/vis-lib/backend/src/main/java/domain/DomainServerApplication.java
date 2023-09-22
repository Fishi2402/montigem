package domain;

import de.se_rwth.commons.logging.Log;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

@SpringBootApplication(
    exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages = {"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest"}
)
public class DomainServerApplication extends domain.DomainServerApplicationTOP {

  public static void main(String[] args) {
    SpringApplication.run(DomainServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    createDummyData();
  }

  public void createDummyData() {
    Address a;
    a = addAddress("Berlin", "Gitschiner Str.", 9);
    addPerson("Alice", LocalDate.of(2003, 1, 12), a);
    a = addAddress("München", "Lichtenfelser Str.", 18);
    addPerson("Bob", LocalDate.of(2000, 3, 10), a);
    a = addAddress("New York", "Springbrook Rd.", 18);
    addPerson("Charles", LocalDate.of(1993, 8, 30), a);
    a = addAddress("Istanbul", "Malazgirt Cd.", 18);
    addPerson("Mert", LocalDate.of(1998, 10, 9), a);
    a = addAddress("Aachen", "Ahornstraße", 55);
    addPerson("Nico", LocalDate.of(2016, 11, 1), a);
    addPerson("Helen", LocalDate.of(1010, 10, 10), a);
  }

  protected Address addAddress(String city, String street, int house) {
    return DomainManager.addressBuilder()
        .street(street)
        .house(house)
        .build()
        .get();
  }

  protected Person addPerson(String name, LocalDate birthday, Address address) {
    return DomainManager.personBuilder()
        .name(name)
        .birthday(birthday)
        .address(address)
        .build()
        .get();
  }

}
