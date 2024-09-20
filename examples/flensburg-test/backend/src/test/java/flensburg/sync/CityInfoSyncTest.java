/* (c) https://github.com/MontiCore/monticore */
package flensburg.sync;

// RTE-T: Testet Infrastruktur für die Synchronisation
// HWC-GT: Testet synchronisation der Klasse CityInfo

import hardy.flensburg.Address;
import hardy.flensburg.Car;
import hardy.flensburg.CityInfo;
import hardy.flensburg.FlensburgManager;
import hardy.flensburg.Person;
import hardy.flensburg.test_infrastructure.FlensburgMockTestSetup;
import org.junit.jupiter.api.Test;
import umlp.test_rte.ClientSetup;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CityInfoSyncTest extends FlensburgMockTestSetup {

  public CityInfoSyncTest() {
    super(ClientSetup.of(1));
  }

  @Test
  public void testCityInfoCanBeCalculatedInServer(){

    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Person p1 = FlensburgManager.personBuilder().mainResidence(addr).age(20).nickname("Smith").build().get();

    Address addr2 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    Person p2 = FlensburgManager.personBuilder().mainResidence(addr2).age(20).nickname("Doe").build().get();

    Car c1 = FlensburgManager.carBuilder().wheelLeft(true).build().get();
    Car c2 = FlensburgManager.carBuilder().wheelLeft(true).build().get();
    Car c3 = FlensburgManager.carBuilder().wheelLeft(true).build().get();
    p1.addOwns(c1);
    p1.addOwns(c2);
    p1.addOwns(c3);

    Car c4 = FlensburgManager.carBuilder().wheelLeft(true).build().get();
    p2.addOwns(c4);


    CityInfo cityInfo = new CityInfo();
    assertNotNull(cityInfo);

    assertEquals(2, cityInfo.calcNumberOfCities());
    assertEquals(4, cityInfo.calcTotalNumberOfCars());
    assertEquals("Flensburg", cityInfo.calcCityWithMostCars());
    assertEquals(p1, cityInfo.calcPersonWithMostCars());
    assertEquals(2, cityInfo.calcAverageNumberOfCars());

    List<String> cities = cityInfo.calcCities();
    assertEquals(2, cities.size());
    assertTrue(cities.contains("Aachen"));
    assertTrue(cities.contains("Flensburg"));
  }

  @Test
  public void testCityInfoCanBeCalculatedInClient(){

    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Person p1 = FlensburgManager.personBuilder().mainResidence(addr).age(20).nickname("Smith").build().get();

    Address addr2 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    Person p2 = FlensburgManager.personBuilder().mainResidence(addr2).age(20).nickname("Doe").build().get();

    Car c1 = FlensburgManager.carBuilder().wheelLeft(true).build().get();
    Car c2 = FlensburgManager.carBuilder().wheelLeft(true).build().get();
    Car c3 = FlensburgManager.carBuilder().wheelLeft(true).build().get();
    p1.addOwns(c1);
    p1.addOwns(c2);
    p1.addOwns(c3);

    Car c4 = FlensburgManager.carBuilder().wheelLeft(true).build().get();
    p2.addOwns(c4);

    getClients(0).activate();
    CityInfo cityInfo = new CityInfo();
    assertNotNull(cityInfo);

    assertEquals(2, cityInfo.calcNumberOfCities());
    assertEquals(4, cityInfo.calcTotalNumberOfCars());
    assertEquals("Flensburg", cityInfo.calcCityWithMostCars());
    assertEquals(p1.getGemId(), cityInfo.calcPersonWithMostCars().getGemId());
    assertEquals(2, cityInfo.calcAverageNumberOfCars());

    List<String> cities = cityInfo.calcCities();
    assertEquals(2, cities.size());
    assertTrue(cities.contains("Aachen"));
    assertTrue(cities.contains("Flensburg"));
  }
}
