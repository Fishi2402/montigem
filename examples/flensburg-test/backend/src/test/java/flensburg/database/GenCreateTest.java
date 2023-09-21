/* (c) https://github.com/MontiCore/monticore */
package flensburg.database;

import com.google.common.base.Stopwatch;
import hardy.flensburg.Address;
import hardy.flensburg.AddressDAO;
import hardy.flensburg.Car;
import hardy.flensburg.CarDAO;
import hardy.flensburg.FlensburgManager;
import hardy.flensburg.Human;
import hardy.flensburg.HumanBuilder;
import hardy.flensburg.HumanDAO;
import hardy.flensburg.Person;
import hardy.flensburg.PersonBuilder;
import hardy.flensburg.PersonDAO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import umlp.backendrte.common.DatabaseAccessManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenCreateTest extends GenAbstractDBTest {

  @Disabled
  @Test
  public void insertManyPersons(){

    Stopwatch watch = Stopwatch.createStarted();
    for(int i = 0; i < 20000; i++){
      Address a1 = FlensburgManager.addressBuilder().city("Flensburg").build().get();
      Person p1 = FlensburgManager.personBuilder().age(20).nickname("Jane").mainResidence(a1).build().get();
    }
    watch.stop();
    Long time = watch.elapsed(TimeUnit.MILLISECONDS);

    assert(2000 > time);
  }

  @Test
  public void testCarDAO() {

    Optional<Car> carOpt = FlensburgManager.carBuilder()
        .wheelLeft(true)
        .namesOfAllParts(Arrays.asList("Mercedes Stern", "Bluetooth Radio", "17 Zoll Fernseher")).build();
    assertTrue(carOpt.isPresent());

    long gemId = carOpt.get().getGemId();
    CarDAO carDAO = new CarDAO();
    Optional<Car> carFromDBOpt = carDAO.selectCar(gemId);

    assertTrue(carFromDBOpt.isPresent());

    assertTrue(carFromDBOpt.get().isWheelLeft());
    assertTrue(carFromDBOpt.get().getNamesOfAllPartsList().containsAll(carOpt.get().getNamesOfAllPartsList()));
  }

  @Test
  public void testHumanDaoInsertAndSelect() {
    long gemId = 1001;
    int age = 20;
    HumanBuilder builder = new MockHumanBuilder(gemId);
    builder.age(age);
    Human h1001 = builder.build().get();

    HumanDAO dao = new HumanDAO();
    dao.insertHuman(h1001);

    Optional<Human> h1001CopyOpt = dao.selectHuman(h1001.getGemId());
    assertTrue(h1001CopyOpt.isPresent());

    Human h1001Copy = h1001CopyOpt.get();
    assertNotSame(h1001, h1001Copy);

    assertEquals(h1001.getGemId(), h1001Copy.getGemId());
    assertEquals(h1001.getAge(), h1001Copy.getAge());
  }

  @Test
  public void testHumanLoadFromDb(){
    long gemId = 1002;
    int age = 20;
    HumanBuilder builder = new MockHumanBuilder(gemId);
    builder.age(age);
    Human h1001db = builder.build().get();

    HumanDAO dao = new HumanDAO();
    dao.insertHuman(h1001db);

    Human h1001 = FlensburgManager.getHumanSure(gemId);
    assertNotSame(h1001db, h1001);
    assertEquals(gemId, h1001.getGemId());
    assertEquals(age, h1001.getAge());
  }


  // Test fuer 1 zu 1 Assoziationen
  @Test
  public void testMainResidence(){
    Car car = FlensburgManager.carBuilder() //db observer insert into db
        .wheelLeft(true)
        .namesOfAllParts(Arrays.asList("Mercedes Stern", "Bluetooth Radio", "17 Zoll Fernseher")).build().get();
    Set<Car> cars = new HashSet<>();
    cars.add(car);
    Address a1 = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Person p1 = FlensburgManager.personBuilder().age(20).nickname("John").owns(cars).mainResidence(a1).build().get();

    PersonDAO personDAO = new PersonDAO();

    Optional<Person> p1CopyOpt = personDAO.selectPerson(p1.getGemId());
    assertTrue(p1CopyOpt.isPresent());
    Person p1Copy = p1CopyOpt.get();

    assertNotSame(p1, p1Copy);
    assertEquals(p1.getGemId(), p1Copy.getGemId());
    assertEquals(p1.getAge(), p1Copy.getAge());
    assertEquals(p1.getNickname(), p1Copy.getNickname());
    assertEquals(p1.getMainResidenceGem().getGemId(), p1Copy.getMainResidenceGem().getGemId());

    assertSame(a1, p1Copy.getMainResidence());
  }

  // Same tests but with HumanDbObserver
  @Test
  public void testHumanCreate(){
    int age = 20;
    Human h = FlensburgManager.humanBuilder().age(age).build().get();

    HumanDAO dao = new HumanDAO();
    Optional<Human> hCopyOpt = dao.selectHuman(h.getGemId());
    assertTrue(hCopyOpt.isPresent());
    Human hCopy = hCopyOpt.get();

    assertNotSame(h, hCopy);
    assertEquals(age, h.getAge());
    assertEquals(h.getGemId(), hCopy.getGemId());
  }

  // Tests fuer Many to Many Assoziationen
  @Test
  public void testCreatePersonWithVisited(){
    Address a1 = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Address a2 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    PersonBuilder pb = FlensburgManager.personBuilder().age(20).nickname("John").mainResidence(a1);
    Person p = pb.build().get();
    pb.addVisited(a1);
    pb.addVisited(a2);

    PersonDAO personDAO = new PersonDAO();

    Optional<Person> pCopyOpt = personDAO.selectPerson(p.getGemId());
    assertTrue(pCopyOpt.isPresent());

    Person pCopy = pCopyOpt.get();
    assertNotSame(p, pCopy);

    assertEquals(2, pCopy.sizeVisiteds());
  }

  @Test
  public void testCreateAddressWithVisitors(){
    Address a1 = FlensburgManager.addressBuilder().city("Flensburg").build().get();

    PersonBuilder pb1 = FlensburgManager.personBuilder().age(20).nickname("John").mainResidence(a1);
    Person p1 = pb1.build().get();

    PersonBuilder pb2 = FlensburgManager.personBuilder().age(25).nickname("Doe").mainResidence(a1);
    Person p2 = pb2.build().get();

    Address a2 = FlensburgManager.addressBuilder().city("Aachen").visitorGemListIds(List.of(p1.getGemId(), p2.getGemId())).build().get();

    AddressDAO addressDAO = new AddressDAO();

    Optional<Address> a2CopyOpt = addressDAO.selectAddress(a2.getGemId());
    assertTrue(a2CopyOpt.isPresent());

    Address a2Copy = a2CopyOpt.get();
    assertNotSame(a2, a2Copy);

    assertEquals(2, a2Copy.sizeVisitors());
  }
}
