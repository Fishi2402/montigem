/* (c) https://github.com/MontiCore/monticore */
package flensburg.gui.showcityinfo;

// HW-T

import flensburg.gui.AbstractGUITest;
import org.junit.jupiter.api.Test;
import umlp.test_rte.ClientSetup;

/* Test GUI (or later also E2E) for city info
*/
public class ShowCityInfoTest extends AbstractGUITest {

  public ShowCityInfoTest() {
    super(ClientSetup.of(1));
  }

  @Test
  public void showCityInfoTest() {
    // eine Ausbaustufe wäre: einen Validator hinzufügen und wenn der failed,
    // zusaetlich eine Fehlermeldung anzeigen (die analog angezeigt wird)

/* TODO MB
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
    assertEquals("theo", ci.getTable().getPersonWithMostCars().getNickname());*/
  }

}
