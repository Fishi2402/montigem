package hardy;
//+ HWC-PT

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CityInfoTest {

  @Test
  public void testCityInfo(){
    FlensburgMock.init();

    Car c1 = FlensburgManager.carBuilder();
    Car c2 = FlensburgManager.carBuilder();

    Car c3 = FlensburgManager.carBuilder();
    Car c4 = FlensburgManager.carBuilder();
    Car c5 = FlensburgManager.carBuilder();
    Car c6 = FlensburgManager.carBuilder();

    Address a1 = FlensburgManager.addressBuilder().city("Aachen").build();
    Address a2 = FlensburgManager.addressBuilder().city("Flensburg").build();

    Person p1 = FlensburgManager
        .personBuilder()
        .nickname("1")
        .mainResidence(a1)
        .age(18)
        .ownsAdd(c1)
        .ownsAdd(c2)
        .build();

    Person p2 = FlensburgManager
        .personBuilder()
        .nickname("2")
        .mainResidence(a2)
        .age(40)
        .ownsAdd(c3)
        .ownsAdd(c4)
        .ownsAdd(c5)
        .ownsAdd(c6)
        .build();


    CityInfo cityInfo = FlensburgManager.getCityInfo();

    assertNotNull(cityInfo);
    assertEquals(2, cityInfo.getNumberOfCities());
    assertEquals(6, cityInfo.getTotalNumberOfCars());
    assertEquals("Flensburg", cityInfo.getCityWithMostCars());
    assertEquals(p2, cityInfo.getPersonWithMostCars());
    assertEquals(3, cityInfo.getAverageNumberOfCars());
  }
}
