package flensburg;//+ HWC-PT

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CityInfoTest {

  @BeforeEach
  public void clear(){
    FlensburgTestContext.clear();
  }

  @Test
  public void testCityInfo(){
    FlensburgMock.init();

    CarBuilder c1 = FlensburgManager.carBuilder().wheelLeft(false).namesOfAllParts(new ArrayList<>());
    CarBuilder c2 = FlensburgManager.carBuilder().wheelLeft(false).namesOfAllParts(new ArrayList<>());

    CarBuilder c3 = FlensburgManager.carBuilder().wheelLeft(false).namesOfAllParts(new ArrayList<>());
    CarBuilder c4 = FlensburgManager.carBuilder().wheelLeft(false).namesOfAllParts(new ArrayList<>());
    CarBuilder c5 = FlensburgManager.carBuilder().wheelLeft(false).namesOfAllParts(new ArrayList<>());
    CarBuilder c6 = FlensburgManager.carBuilder().wheelLeft(false).namesOfAllParts(new ArrayList<>());

    Address a1 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    Address a2 = FlensburgManager.addressBuilder().city("Flensburg").build().get();

    PersonBuilder p1 = FlensburgManager
        .personBuilder()
        .nickname("1")
        .mainResidence(a1)
        .age(18);

    PersonBuilder p2 = FlensburgManager
        .personBuilder()
        .nickname("2")
        .mainResidence(a2)
        .age(40);

    p1.ownsAdd(c1).ownsAdd(c2).build();
    p2.ownsAdd(c3).ownsAdd(c4).ownsAdd(c5).ownsAdd(c6).build();

    CityInfo cityInfo = FlensburgManager.getCityInfo();

    assertNotNull(cityInfo);
    assertEquals(2, cityInfo.getNumberOfCities());
    assertEquals(6, cityInfo.getTotalNumberOfCars());
    assertEquals("Flensburg", cityInfo.getCityWithMostCars());
    assertEquals(p2, cityInfo.getPersonWithMostCars());
    assertEquals(3, cityInfo.getAverageNumberOfCars());
  }
}
