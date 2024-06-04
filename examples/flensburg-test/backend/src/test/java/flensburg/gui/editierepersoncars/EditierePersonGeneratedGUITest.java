/* (c) https://github.com/MontiCore/monticore */
package flensburg.gui.editierepersoncars;

// HW-T

import de.se_rwth.commons.logging.Log;
import flensburg.gui.AbstractGUITest;
import hardy.flensburg.Address;
import hardy.flensburg.FlensburgManager;
import hardy.flensburg.FlensburgMock;
import hardy.flensburg.Person;
import hardy.flensburg.PersonErrorCollector;
import hardy.flensburg.PersonErrorObserver;
import org.junit.jupiter.api.Test;
import stanley.personedit.PersonEdit;
import umlp.backendrte.test_infrastructure.ClientSetup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/* Test GUI (also E2E) for Person editing using generated java-gui from this gui-model:

package stanley.editiereperson;

import hardy.Flensburg.Person;
import umlp.gui.TextInput;
import umlp.gui.NumberInput;
import umlp.gui.Button;

import stanley.editierepersoncars.EditierePersonCars;

page EditierePerson(Person p) {
  @TextInput(value = p.nickname);
  @NumberInput(value = p.age);
  @Button(text = "Ok", leftClick = navigate(@EditierePersonCars(person = p)));
  @Button(text = "Decrease", leftClick = decreaseAge());
}   */

public class EditierePersonGeneratedGUITest extends AbstractGUITest {

  public EditierePersonGeneratedGUITest() {
    super(ClientSetup.of(2));
  }

  /**
   * Changes in the fields of a GUI page change the object and vice versa
   */
  @Test
  public void editPersonGUITest() {
    Address a = FlensburgMock.addressBuilder().city("Flensburg").build().get();
    // simulate two instances of a person located on two different clients
    Person p1 = FlensburgMock.personBuilder()
        .age(24)
        .nickname("theo")
        .mainResidence(a)
        .build().get();
    PersonEdit e = new PersonEdit();
    e.setP(p1);

    // Check GUI contains correct values
    assertEquals("theo", e.getGemTextInput4Entry());
    assertEquals("24", e.getGemTextInput5Entry());

    // Check setting the age in the gui changes the age of the person
    e.setGemTextInput5Entry("33");
    assertEquals(33, p1.getAge().intValue());

    // Check decrease button decreases age in GUI and Object
    e.gemButton6LeftClick(null);
    e.gemButton6LeftClick(null);
    assertEquals("31", e.getGemTextInput5Entry());
    assertEquals(31, p1.getAge().intValue());
  }

  /**
   * Valid changes in one client propagates to other clients
   */
  @Test
  public void observePersonGUITest() {
    // a - address, aGemId - gem id of address
    long aGemId = 1001L;
    // p - person, pGemId - gem id of person
    long pGemId = 1002L;

    // working with client 1
    getClients(0).activate();
    // create initial person with an address
    Address a = FlensburgManager.addressBuilderFromId(aGemId).city("Flensburg").build().get();
    Person p1 = FlensburgManager.personBuilderFromId(pGemId)
        .age(24)
        .nickname("theo")
        .mainResidence(a)
        .owns(Collections.emptySet())
        .build().get();
    // page opened by a user on client 1
    PersonEdit e1 = new PersonEdit();
    e1.setP(p1);

    // working with client 2
    getClients(1).activate();
    // check that person is also available for client 2
    Optional<Person> pClient2Opt = FlensburgManager.personOpt(pGemId);
    assertTrue(pClient2Opt.isPresent());
    Person p2 = pClient2Opt.get();
    // page opened by a user on client 2
    PersonEdit e2 = new PersonEdit();
    e2.setP(p2);
    // TODO AGe: strategy on input fields update, check current object/attribute status

    getClients(0).activate();
    // check setting the age in GUI of client 1 changes the input value for GUI of client 2
    e1.setGemTextInput5Entry("33");
    assertEquals("33", e2.getGemTextInput5Entry());

    getClients(1).activate();
    // check decrease button click in GUI of client 2 decreases age in GUI of client 1
    e2.gemButton6LeftClick(null);
    e2.gemButton6LeftClick(null);
    assertEquals("31", e1.getGemTextInput5Entry());
  }

  /**
   * Error (invalid state) triggered on server will be shown in the client
   * May happen when client requires calculation on the server
   */
  @Test
  public void observeServerToClientErrorTest() {
    Log.initDEBUG();

    // create initial person with an address
    long gemId = 1000L;
    Address a = FlensburgManager.addressBuilderFromId(gemId).city("Flensburg").build().get();
    gemId = 1001L;
    FlensburgManager
        .personBuilderFromId(gemId)
        .age(24)
        .nickname("theo")
        .mainResidence(a)
        .owns(Collections.emptySet())
        .build();

    // work on client
    getClients(0).activate();
    Person p1001C1 = FlensburgManager.getPersonSure(gemId);
    PersonEdit e1 = new PersonEdit();
    e1.setP(p1001C1);

    // work on server
    getBackend().activate();
    Person p1001S = FlensburgManager.getPersonSure(gemId);
    // set invalid age
    p1001S.setAge(15);
    // TODO MB assertEquals(24, p1001S.getAge());

    getClients(0).activate();
    // TODO MB assertEquals(24, p1001C1.getAge());
  }

  /**
   * Error (invalid state) triggered on client will be not be propagated to server
   */
  @Test
  public void observeClientToServerErrorTest() {
    Log.initDEBUG();

    // create initial person with an address
    long gemId = 1000L;
    Address a = FlensburgManager.addressBuilderFromId(gemId).city("Flensburg").build().get();
    gemId = 1001L;
    FlensburgManager
        .personBuilderFromId(gemId)
        .age(24)
        .nickname("theo")
        .mainResidence(a)
        .owns(Collections.emptySet())
        .build();

    // add error collector for server to track errors
    List<String> serverErrorMsgs = new ArrayList<>();
    PersonErrorCollector.addErrorObserver(new PersonErrorObserver() {
      @Override
      public void notifyAgeInvalid(Person person, String msg) {
        serverErrorMsgs.add(msg);
      }
    });

    // work on client
    getClients(0).activate();
    Person p1001C1 = FlensburgManager.getPersonSure(gemId);
    PersonEdit e1 = new PersonEdit();
    e1.setP(p1001C1);
    // put invalid value into input field
    e1.setGemTextInput5Entry("15");

    // work on server
    getBackend().activate();
    Person p1001S = FlensburgManager.getPersonSure(gemId);
    // invalid value is not propagated from client to server
    // TODO MB assertEquals(24, p1001S.getAge());
    // neither do errors
    // TODO MB assertTrue(serverErrorMsgs.isEmpty());
  }

}
