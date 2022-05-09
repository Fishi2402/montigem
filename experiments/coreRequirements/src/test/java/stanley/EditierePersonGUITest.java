package stanley;

// HW-T

/* This is sime code that tests the GUI (or later also E2E)
   functionality within Java:
*/


import hardy.FlensburgManager;
import hardy.FlensburgMock;
import hardy.Person;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EditierePersonGUITest {

  @Test
  public void runATest() {
    // eine Ausbaustufe wäre: einen Validator hinzufügen und wenn der failed,
    // zusaetlich eine Fehlermeldung anzeigen (die analog angezeigt wird)
    FlensburgMock.init();

    Person p = FlensburgManager.personBuilder().age(24).nickname("theo").build();
    EditierePersonGUI e = new EditierePersonGUI(p);

    // Check GUI has contains correct values
    assertEquals("theo", e.getTextInput());
    assertEquals(24, e.getNumberInput());

    // Check setting the age in the gui changes the age of the person
    e.setNumberInput(33);
    assertEquals(33, p.getAge());

    // Check decrease button decreases age in GUI and Object
    e.pressButtonDecrease();
    e.pressButtonDecrease();
    assertEquals(31, e.getNumberInput());
    assertEquals(31, p.getAge());

    // und später kombinieren wir das mit Server/Client kommunikation und prüefen dann
    // nicht nur am lokalen Objekt, sondern simuliert im Gesamten System die Konsistente Änderung
  }
}
