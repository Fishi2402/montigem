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
    st = createStock(LocalDate.of(2022, 1, 1), 8, "MontiCore", 80);
    alice.addStocks(st);
    john.addStocks(st);
    st = createStock(LocalDate.of(2022, 1, 1), 9, "MontiCore", 144);
    olivia.addStocks(st);
    michelle.addStocks(st);
    st = createStock(LocalDate.of(2022, 1, 1), 10, "MontiCore", 153);
    emma.addStocks(st);
    boris.addStocks(st);
    st = createStock(LocalDate.of(2022, 1, 1), 11, "MontiCore", 136);
    isabella.addStocks(st);
    john.addStocks(st);

    st = createStock(LocalDate.of(2022, 1, 2), 8, "MontiCore", 90);
    emma.addStocks(st);
    john.addStocks(st);
    st = createStock(LocalDate.of(2022, 1, 2), 9, "MontiCore", 133);
    isabella.addStocks(st);
    michelle.addStocks(st);
    st = createStock(LocalDate.of(2022, 1, 2), 10, "MontiCore", 144);
    alice.addStocks(st);
    boris.addStocks(st);
    st = createStock(LocalDate.of(2022, 1, 2), 11, "MontiCore", 133);
    olivia.addStocks(st);
    bob.addStocks(st);

    st = createStock(LocalDate.of(2022, 1, 3), 8, "MontiCore", 143);
    isabella.addStocks(st);
    boris.addStocks(st);
    st = createStock(LocalDate.of(2022, 1, 3), 9, "MontiCore", 125);
    olivia.addStocks(st);
    michelle.addStocks(st);
    st = createStock(LocalDate.of(2022, 1, 3), 10, "MontiCore", 143);
    alice.addStocks(st);
    bob.addStocks(st);
    st = createStock(LocalDate.of(2022, 1, 3), 11, "MontiCore", 123);
    emma.addStocks(st);
    john.addStocks(st);

    st = createStock(LocalDate.of(2022, 1, 4), 8, "MontiCore", 100);
    isabella.addStocks(st);
    charles.addStocks(st);
    st = createStock(LocalDate.of(2022, 1, 4), 9, "MontiCore", 140);
    alice.addStocks(st);
    thomas.addStocks(st);
    st = createStock(LocalDate.of(2022, 1, 4), 10, "MontiCore", 150);
    emma.addStocks(st);
    bob.addStocks(st);
    st = createStock(LocalDate.of(2022, 1, 4), 11, "MontiCore", 123);
    michelle.addStocks(st);
    john.addStocks(st);
    

    st = createStock(LocalDate.of(2022, 1, 5), 8, "MontiCore", 136);
    emma.addStocks(st);
    st = createStock(LocalDate.of(2022, 1, 5), 9, "MontiCore", 150);
    olivia.addStocks(st);
    john.addStocks(st);
    st = createStock(LocalDate.of(2022, 1, 5), 10, "MontiCore", 160);
    alice.addStocks(st);
    st = createStock(LocalDate.of(2022, 1, 5), 11, "MontiCore", 132);
    isabella.addStocks(st);
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

  protected Stock createStock(LocalDate date, int hour, String name, int price) {
    return DomainManager.stockBuilder()
        .date(date)
        .hour(hour)
        .name(name)
        .price(price)
        .build()
        .get();
  }

}
