package stanley.sync;

// RTE-T: Testet Infrastruktur für die Synchronisation
// HWC-GT: Testet synchronisation der Klasse CityInfo
import hardy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanley.FlensburgManagerClientImplBuilder;
import stanley.FlensburgTestContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CityInfoSyncTest {
  @BeforeEach
  public void clear(){
    FlensburgTestContext.clear();
  }

  @Test
  public void testCityInfoCanBeCalculatedInServer(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(startGemId);

    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Person p1 = FlensburgManager.personBuilder().mainResidence(addr).age(20).nickname("Smith").build().get();

    Address addr2 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    Person p2 = FlensburgManager.personBuilder().mainResidence(addr2).age(20).nickname("Doe").build().get();


    FlensburgMock.secretlyAdaptnextGemIds(3001L);
    Car c1 = FlensburgManager.carBuilder().owns(p1).build().get();
    Car c2 = FlensburgManager.carBuilder().owns(p1).build().get();
    Car c3 = FlensburgManager.carBuilder().owns(p1).build().get();

    Car c4 = FlensburgManager.carBuilder().owns(p2).build().get();


    CityInfo cityInfo = FlensburgManager.getCityInfo();
    assertNotNull(cityInfo);

    assertEquals(2, cityInfo.getNumberOfCities());
    assertEquals(4, cityInfo.getTotalNumberOfCars());
    assertEquals("Flensburg", cityInfo.getCityWithMostCars());
    assertEquals(p1, cityInfo.getPersonWithMostCars());
    assertEquals(2, cityInfo.getAverageNumberOfCars());

    List<String> cities = cityInfo.getCities();
    assertEquals(2, cities.size());
    assertTrue(cities.contains("Aachen"));
    assertTrue(cities.contains("Flensburg"));
  }

  @Test
  public void testCityInfoCanBeCalculatedInClient(){
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    FlensburgTestContext clientContext1 = new FlensburgManagerClientImplBuilder().backendContext(serverContext).build().get();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(startGemId);

    Address addr = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Person p1 = FlensburgManager.personBuilder().mainResidence(addr).age(20).nickname("Smith").build().get();

    Address addr2 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    Person p2 = FlensburgManager.personBuilder().mainResidence(addr2).age(20).nickname("Doe").build().get();


    FlensburgMock.secretlyAdaptnextGemIds(3001L);
    Car c1 = FlensburgManager.carBuilder().owns(p1).build().get();
    Car c2 = FlensburgManager.carBuilder().owns(p1).build().get();
    Car c3 = FlensburgManager.carBuilder().owns(p1).build().get();

    Car c4 = FlensburgManager.carBuilder().owns(p2).build().get();

    clientContext1.activate();
    CityInfo cityInfo = FlensburgManager.getCityInfo();
    assertNotNull(cityInfo);

    assertEquals(2, cityInfo.getNumberOfCities());
    assertEquals(4, cityInfo.getTotalNumberOfCars());
    assertEquals("Flensburg", cityInfo.getCityWithMostCars());
    assertEquals(p1.getGemId(), cityInfo.getPersonWithMostCars().getGemId());
    assertEquals(2, cityInfo.getAverageNumberOfCars());

    List<String> cities = cityInfo.getCities();
    assertEquals(2, cities.size());
    assertTrue(cities.contains("Aachen"));
    assertTrue(cities.contains("Flensburg"));
  }
}
