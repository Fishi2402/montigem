/* (c) https://github.com/MontiCore/monticore */
package stanley.neuepersonanlegen;

// HW-T

import hardy.FlensburgMock;
import org.junit.jupiter.api.Test;
import stanley.AbstractGUITest;

import static org.junit.jupiter.api.Assertions.*;

/* Test GUI (also E2E) for creation of Person using generated java-gui from this gui-model:
package stanley.neuepersonanlegen;

import hardy.Flensburg.Person;
import umlp.gui.TextInput;
import umlp.gui.NumberInput;
import umlp.gui.Button;
import stanley.editierepersoncars.EditierePersonCars;

page NeuePersonAnlegen(Person p) { // "leere" Person
  @TextInput(value = p.nickname);
  @NumberInput(value = p.age);
  @Button(text = "Ok", leftClick = navigate(@EditierePersonCars(person=p)));
  @Button(text = "Decrease", leftClick = decreaseAge());
}
 */
public class NeuePersonAnlegenGeneratedGUITest extends AbstractGUITest {

  @Test
  public void createNewPersonTest() {
    // eine Ausbaustufe wäre: einen Validator hinzufügen und wenn der failed,
    // zusaetlich eine Fehlermeldung anzeigen (die analog angezeigt wird)
    FlensburgMock.init();

    NeuePersonAnlegenCreate e = new NeuePersonAnlegenCreate();

    // Check GUI initial values
    assertNull(e.pBuilder.getAge());

    // Check setting the age in the gui changes the age of the person
    e.setNumberInputComp2(33);
    assertTrue(e.pBuilder.isAgePresent());
    assertEquals(33, e.pBuilder.getAge().intValue());

    // Check decrease button decreases age in GUI and Object
    e.leftClickButtonComp5();
    e.leftClickButtonComp5();
    assertTrue(e.pBuilder.isAgePresent());
    assertEquals(31, e.pBuilder.getAge().intValue());
    assertEquals(31, e.getNumberInputComp2());

    // und später kombinieren wir das mit Server/Client kommunikation und prüefen dann
    // nicht nur am lokalen Objekt, sondern simuliert im Gesamten System die Konsistente Änderung
  }
}
