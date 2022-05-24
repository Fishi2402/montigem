package stanley.newpersonanlegen;

// HW-T

/* This is sime code that tests the GUI (or later also E2E)
   functionality within Java:
*/


import hardy.FlensburgMock;
import org.junit.Test;
import stanley.newpersonanlegen.NeuePersonAnlegenGUI;

import static org.junit.Assert.*;

public class NeuePersonAnlegenGUITest {

  @Test
  public void openNeuePersonAnlegenTest() {
    // eine Ausbaustufe wäre: einen Validator hinzufügen und wenn der failed,
    // zusaetlich eine Fehlermeldung anzeigen (die analog angezeigt wird)
    FlensburgMock.init();

    NeuePersonAnlegenGUI e = new NeuePersonAnlegenGUI();

    // Check GUI initial values
    // TODO for GUI having 0 as initial age is not the best option, it should be null or
    //  Optional.empty(), because 0 could be a valid option, which user doesn't want.
    //  This means that age either has to have type Optional<Integer> or Integer instead of int,
    //  because Java will indicate contract violation if we expect a method  which returns int
    //  to return null
    // assertNull(e.pb.getAge());

    // Check setting the age in the gui changes the age of the person
    e.setNumberInput(33);
    assertTrue(e.pb.isAgePresent());
    assertEquals(33, e.pb.getAge());

    // Check decrease button decreases age in GUI and Object
    e.pressButtonDecrease();
    e.pressButtonDecrease();
    assertTrue(e.pb.isAgePresent());
    assertEquals(31, e.pb.getAge());
    assertEquals(31, e.getNumberInput());

    // und später kombinieren wir das mit Server/Client kommunikation und prüefen dann
    // nicht nur am lokalen Objekt, sondern simuliert im Gesamten System die Konsistente Änderung
  }
}
