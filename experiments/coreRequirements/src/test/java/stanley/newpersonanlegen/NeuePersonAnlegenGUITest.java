package stanley.newpersonanlegen;

// HW-T

import hardy.FlensburgMock;
import org.junit.jupiter.api.Test;
import stanley.AbstractGUITest;

import static org.junit.jupiter.api.Assertions.*;

/* Tests GUI (or later also E2E) for creation of Person
*/
public class NeuePersonAnlegenGUITest extends AbstractGUITest {

  @Test
  public void createNewPersonTest() {
    // eine Ausbaustufe wäre: einen Validator hinzufügen und wenn der failed,
    // zusaetlich eine Fehlermeldung anzeigen (die analog angezeigt wird)
    FlensburgMock.init();

    NeuePersonAnlegenGUI e = new NeuePersonAnlegenGUI();

    // Check GUI initial values
    assertNull(e.pb.getAge());

    // Check setting the age in the gui changes the age of the person
    e.setNumberInput(33);
    assertTrue(e.pb.isAgePresent());
    assertEquals(33, e.pb.getAge().intValue());

    // Check decrease button decreases age in GUI and Object
    e.pressButtonDecrease();
    e.pressButtonDecrease();
    assertTrue(e.pb.isAgePresent());
    assertEquals(31, e.pb.getAge().intValue());
    assertEquals(31, e.getNumberInput());

    // und später kombinieren wir das mit Server/Client kommunikation und prüefen dann
    // nicht nur am lokalen Objekt, sondern simuliert im Gesamten System die Konsistente Änderung
  }
}
