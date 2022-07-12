package stanley.editierepersoncars;

// HW-T

import de.se_rwth.commons.logging.Log;
import hardy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanley.AbstractGUITest;
import stanley.FlensburgManagerClientImplBuilder;
import stanley.FlensburgTestContext;
import stanley.editiereperson.EditierePersonGUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/* Test GUI (or later also E2E) for Person editing
 */
public class EditierePersonGUITest extends AbstractGUITest {

  @BeforeEach
  public void clear() {
    FlensburgTestContext.clear();
    FlensburgMock.init();
  }

  @Test
  public void editPersonGUITest() {
    Address a = FlensburgMock.addressBuilder().city("Flensburg").build().get();
    // simulate two instances of a person located on two different clients
    Person p1 = FlensburgMock.personBuilder()
        .age(24)
        .nickname("theo")
        .mainResidence(a)
        .build().get();
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
    // simulate two instances of a person located on two different clients
    FlensburgTestContext backendContext = FlensburgTestContext.fromCurrentContext();
    long aGemId = 1001L;
    long pGemId = 1002L;

    FlensburgTestContext clientContext1 = new FlensburgManagerClientImplBuilder()
        .backendContext(backendContext).build().get();
    FlensburgTestContext clientContext2 = new FlensburgManagerClientImplBuilder()
        .backendContext(backendContext).build().get();

    clientContext1.activate();
    FlensburgMock.secretlyAdaptnextGemIds(aGemId);
    Address a = FlensburgMock.addressBuilder().city("Flensburg").build().get();
    FlensburgMock.secretlyAdaptnextGemIds(pGemId);
    Person p1 = FlensburgMock
        .personBuilder()
        .age(24)
        .nickname("theo")
        .mainResidence(a)
        .owns(Collections.emptySet())
        .build().get();
    // Page 1 opened by one user
    EditierePersonGUI e1 = new EditierePersonGUI(p1);

    clientContext2.activate();
    Optional<Person> pClient2Opt = FlensburgManager.personOpt(pGemId);
    assertTrue(pClient2Opt.isPresent());
    Person p2 = pClient2Opt.get();
    // Page 2 opened by another user
    EditierePersonGUI e2 = new EditierePersonGUI(p2);
    // TODO AGe: strategy on input fields update, check current object/attribute status
    // Check setting the age in the gui changes the input value for other client GUI

    clientContext1.activate();
    e1.setNumberInput(33);
    assertEquals(33, e2.getNumberInput());

    clientContext2.activate();
    // Check decrease button decreases age in both client GUIs
    e2.pressButtonDecrease();
    e2.pressButtonDecrease();
    assertEquals(31, e1.getNumberInput());
  }

  @Test
  public void observeServerPersonErrorGUITest() {
    Log.initDEBUG();

    // Create backend manager
    FlensburgTestContext backendContext = FlensburgTestContext.fromCurrentContext();
    long gemId = 1000L;
    FlensburgMock.secretlyAdaptnextGemIds(gemId);
    Address a = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    gemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(gemId);
    FlensburgManager
        .personBuilder()
        .age(24)
        .nickname("theo")
        .mainResidence(a)
        .owns(Collections.emptySet())
        .build();

    HumanErrorCollector clientErrorCollector = new HumanErrorCollector();
    FlensburgTestContext clientContext1 = new FlensburgManagerClientImplBuilder()
        .backendContext(backendContext)
        .humanErrorCollector(clientErrorCollector)
        .build()
        .get();

    clientContext1.activate();

    Person p1001C1 = FlensburgManager.getPersonSure(gemId);
    EditierePersonGUI e1 = new EditierePersonGUI(p1001C1);

    assertEquals("", e1.getNumberInputError());
    // e1.setNumberInput(15);
    backendContext.activate();
    Person p1001S = FlensburgManager.getPersonSure(gemId);
    p1001S.setAge(15);

    assertEquals("Age 15 of Person with gemId 1001 is invalid!",
        e1.getNumberInputError());
  }

  @Test
  public void observeClientPersonErrorGUITest() {
    Log.initDEBUG();

    // Create backend manager
    FlensburgTestContext backendContext = FlensburgTestContext.fromCurrentContext();
    long gemId = 1000L;
    FlensburgMock.secretlyAdaptnextGemIds(gemId);
    Address a = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    gemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(gemId);
    FlensburgManager
        .personBuilder()
        .age(24)
        .nickname("theo")
        .mainResidence(a)
        .owns(Collections.emptySet())
        .build();

    List<String> serverErrorMsgs = new ArrayList<>();
    PersonErrorCollector.addErrorObserver(new PersonErrorObserver() {
      @Override
      public void notifyAgeInvalid(Person person, String msg) {
        serverErrorMsgs.add(msg);
      }
    });

    HumanErrorCollector clientErrorCollector = new HumanErrorCollector();
    FlensburgTestContext clientContext1 = new FlensburgManagerClientImplBuilder()
        .backendContext(backendContext)
        .humanErrorCollector(clientErrorCollector)
        .build()
        .get();

    clientContext1.activate();

    Person p1001C1 = FlensburgManager.getPersonSure(gemId);
    EditierePersonGUI e1 = new EditierePersonGUI(p1001C1);

    e1.setNumberInput(15);

    assertEquals("Age 15 of Person with gemId 1001 is invalid!",
        e1.getNumberInputError());
    // e1.setNumberInput(15);
    backendContext.activate();
    Person p1001S = FlensburgManager.getPersonSure(gemId);
    assertEquals(24, p1001S.getAge());
    assertTrue(serverErrorMsgs.isEmpty());
  }

}
