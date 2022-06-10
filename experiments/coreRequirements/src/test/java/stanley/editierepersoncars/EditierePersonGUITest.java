package stanley.editierepersoncars;

// HW-T

import hardy.Address;
import hardy.FlensburgMock;
import hardy.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanley.AbstractGUITest;
import stanley.editiereperson.EditierePersonGUI;

import static org.junit.jupiter.api.Assertions.assertEquals;

/* Test GUI (or later also E2E) for Person editing
*/
public class EditierePersonGUITest extends AbstractGUITest {

  protected Person p1;
  protected Person p2;

  @BeforeEach
  public void init() {
    // eine Ausbaustufe wäre: einen Validator hinzufügen und wenn der failed,
    // zusaetlich eine Fehlermeldung anzeigen (die analog angezeigt wird)
    FlensburgMock.init();
    Address a = FlensburgMock.addressBuilder().city("Flensburg").build().get();
    // TODO wir haben keine 2 Personeninstanzen auf dem Server oder auf einem Client, aber jedes
    //  Client hat eigenes Personobjekt.
    // simulate two instances of a person located on two different clients
    FlensburgMock.secretlyAdaptnextGemIds(2222);
    p1 = FlensburgMock.personBuilder().age(24).nickname("theo").mainResidence(a).build().get();
    FlensburgMock.secretlyAdaptnextGemIds(2222);
    p2 = FlensburgMock.personBuilder().age(24).nickname("theo").mainResidence(a).build().get();
  }

  @Test
  public void editPersonGUITest() {
    EditierePersonGUI e = new EditierePersonGUI(p1);

    // Check GUI has contains correct values
    assertEquals("theo", e.getTextInput());
    assertEquals(24, e.getNumberInput().intValue());

    // Check setting the age in the gui changes the age of the person
    e.setNumberInput(33);
    assertEquals(33, p1.getAge().intValue());

    // Check decrease button decreases age in GUI and Object
    e.pressButtonDecrease();
    e.pressButtonDecrease();
    assertEquals(31, e.getNumberInput().intValue());
    assertEquals(31, p1.getAge().intValue());

    // und später kombinieren wir das mit Server/Client kommunikation und prüefen dann
    // nicht nur am lokalen Objekt, sondern simuliert im Gesamten System die Konsistente Änderung
  }

  @Test
  public void observePersonGUITest() {
    // Page 1 opened by one user
    EditierePersonGUI e1 = new EditierePersonGUI(p1);
    // Page 2 opened by another user
    EditierePersonGUI e2 = new EditierePersonGUI(p2);
    // TODO AGe: strategy on input fields update, check current object/attribute status
    // Check setting the age in the gui changes the input value
    e1.setNumberInput(33);
    assertEquals(33, e2.getNumberInput());

   // Check decrease button decreases age in GUI and Object
    e1.pressButtonDecrease();
    e1.pressButtonDecrease();
    assertEquals(31, e2.getNumberInput().intValue());
  }
}
