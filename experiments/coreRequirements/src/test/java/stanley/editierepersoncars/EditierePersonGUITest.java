package stanley.editierepersoncars;

// HW-T

import hardy.Address;
import hardy.FlensburgManager;
import hardy.FlensburgMock;
import hardy.Person;
import org.junit.jupiter.api.Test;
import stanley.AbstractGUITest;
import stanley.editiereperson.EditierePersonGUI;

import static org.junit.jupiter.api.Assertions.assertEquals;

/* Test GUI (or later also E2E) for Person editing
*/
public class EditierePersonGUITest extends AbstractGUITest {

  @Test
  public void editPersonGUITest() {
    // eine Ausbaustufe wäre: einen Validator hinzufügen und wenn der failed,
    // zusaetlich eine Fehlermeldung anzeigen (die analog angezeigt wird)
    FlensburgMock.init();

    Address a = FlensburgManager.addressBuilder().city("Flensburg").build().get();

    Person p = FlensburgManager.personBuilder().age(24).nickname("theo").mainResidence(a).build().get();
    EditierePersonGUI e = new EditierePersonGUI(p);

    // Check GUI has contains correct values
    assertEquals("theo", e.getTextInput());
    assertEquals(24, e.getNumberInput().intValue());

    // Check setting the age in the gui changes the age of the person
    e.setNumberInput(33);
    assertEquals(33, p.getAge().intValue());

    // Check decrease button decreases age in GUI and Object
    e.pressButtonDecrease();
    e.pressButtonDecrease();
    assertEquals(31, e.getNumberInput().intValue());
    assertEquals(31, p.getAge().intValue());

    // und später kombinieren wir das mit Server/Client kommunikation und prüefen dann
    // nicht nur am lokalen Objekt, sondern simuliert im Gesamten System die Konsistente Änderung
  }
}
