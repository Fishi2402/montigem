/* (c) https://github.com/MontiCore/monticore */
package stanley.showcityinfo;

// HW-T

import hardy.*;
import org.junit.jupiter.api.Test;
import stanley.AbstractGUITest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/* Test GUI (or later also E2E) for city info
*/
public class ShowCityInfoTest extends AbstractGUITest {

  @Test
  public void showCityInfoTest() {
    // eine Ausbaustufe wäre: einen Validator hinzufügen und wenn der failed,
    // zusaetlich eine Fehlermeldung anzeigen (die analog angezeigt wird)
    FlensburgMock.init();

    Address a = FlensburgManager.addressBuilder().city("Aachen").build().get();
    CarBuilder cb = FlensburgManager.carBuilder();
    PersonBuilder pb = FlensburgManager.personBuilder()
        .age(24)
        .nickname("theo")
        .mainResidence(a)
        .owns(Collections.singleton(cb));
    cb.build();
    pb.build();

    ShowCityInfo ci = new ShowCityInfo();

    // Check GUI has contains correct values
    assertEquals(1, ci.getTable().getNumberOfCities());
    assertEquals(1, ci.getTable().getAverageNumberOfCars());
    assertEquals(1, ci.getTable().getTotalNumberOfCars());
    assertEquals("Aachen", ci.getTable().getCityWithMostCars());
    assertEquals("theo", ci.getTable().getPersonWithMostCars().getNickname());
  }

}
