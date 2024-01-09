/* (c) https://github.com/MontiCore/monticore */
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

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class }, scanBasePackages = { "umlp.backendrte.service.websocket",
        "umlp.backendrte.service.rest", "service" })
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
    Person alice = addPerson("Alice", LocalDate.of(1990, 1, 12), a);
    a = addAddress("Springfield", "Maple Aven.", 3);
    Person olivia = addPerson("Olivia", LocalDate.of(1991, 5, 6), a);
    a = addAddress("Manchester", "Oak Str.", 29);
    Person emma = addPerson("Emma", LocalDate.of(1990, 4, 10), a);
    a = addAddress("Vancouver", "Pine Rd.", 15);
    Person isabella = addPerson("Isabella", LocalDate.of(1991, 3, 3), a);
    a = addAddress("Varna", "Apple Str.", 15);
    Person michelle = addPerson("Michelle", LocalDate.of(1992, 2, 9), a);
    a = addAddress("Sofia", "Bulgaria Ul.", 1);
    Person john = addPerson("John", LocalDate.of(1991, 10, 1), a);
    a = addAddress("Plovdiv", "April Str.", 1);
    Person boris = addPerson("Boris", LocalDate.of(1992, 11, 3), a);
    a = addAddress("München", "Lichtenfelser Str.", 18);
    Person bob = addPerson("Bob", LocalDate.of(1994, 3, 10), a);
    a = addAddress("New York", "Springbrook Rd.", 18);
    Person charles = addPerson("Charles", LocalDate.of(1995, 8, 3), a);
    a = addAddress("Burgas", "Aleksander Str.", 54);
    Person thomas = addPerson("Thomas", LocalDate.of(1996, 8, 3), a);
    a = addAddress("Istanbul", "Malazgirt Cd.", 18);
    Person mert = addPerson("Mert", LocalDate.of(2000, 10, 9), a);
    a = addAddress("Aachen", "Ahornstraße", 55);
    Person nico = addPerson("Nico", LocalDate.of(2001, 11, 1), a);
    Person helen = addPerson("Helen", LocalDate.of(2002, 10, 10), a);

    alice.addFriends(bob);
    alice.addFriends(nico);

    olivia.addFriends(emma);
    olivia.addFriends(isabella);
    olivia.addFriends(michelle);

    isabella.addFriends(emma);
    isabella.addFriends(olivia);
    isabella.addFriends(michelle);

    emma.addFriends(olivia);
    emma.addFriends(isabella);
    emma.addFriends(michelle);

    michelle.addFriends(olivia);
    michelle.addFriends(emma);
    michelle.addFriends(isabella);

    john.addFriends(charles);
    john.addFriends(nico);

    bob.addFriends(helen);
    bob.addFriends(alice);
    bob.addFriends(nico);

    charles.addFriends(john);

    mert.addFriends(nico);

    nico.addFriends(alice);
    nico.addFriends(mert);
    nico.addFriends(john);
    nico.addFriends(bob);

    helen.addFriends(thomas);
    helen.addFriends(boris);
    helen.addFriends(bob);

    thomas.addFriends(helen);
    thomas.addFriends(boris);

    boris.addFriends(thomas);
    boris.addFriends(helen);

    Stock st;
    st = addStock(LocalDate.of(2022, 1, 2), 126, 144, 153, 136);
    john.addStocks(st);
    st = addStock(LocalDate.of(2022, 2, 2), 142, 133, 144, 133);
    john.addStocks(st);
    st = addStock(LocalDate.of(2022, 3, 2), 138, 140, 150, 123);
    john.addStocks(st);
    st = addStock(LocalDate.of(2022, 4, 2), 143, 125, 143, 123);
    john.addStocks(st);
    st = addStock(LocalDate.of(2022, 5, 2), 136, 150, 160, 132);
    john.addStocks(st);
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

  protected Stock addStock(LocalDate date, int open, int close, int high, int low) {
    return DomainManager.stockBuilder()
        .date(date)
        .open(open)
        .close(close)
        .high(high)
        .low(low)
        .build()
        .get();
  }

}
