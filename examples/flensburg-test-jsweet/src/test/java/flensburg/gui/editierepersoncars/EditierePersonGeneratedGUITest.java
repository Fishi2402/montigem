/* (c) https://github.com/MontiCore/monticore */
package flensburg.gui.editierepersoncars;

// HW-T

import de.se_rwth.commons.logging.Log;
import flensburg.gui.AbstractGUITest;
import hardy.flensburg.Address;
import hardy.flensburg.FlensburgMock;
import hardy.flensburg.FlensburgTestContext;
import hardy.flensburg.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

  @BeforeEach
  public void clear() {
    FlensburgTestContext.clear();
    FlensburgMock.init();
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
    PersonEdit e = new PersonEdit(p1);

    // Check GUI contains correct values
    assertEquals("theo", e.getGemTextInputComp1Value());
    assertEquals(24, e.getGemNumberInputComp2Value());

    // Check setting the age in the gui changes the age of the person
    e.setGemNumberInputComp2Value(33);
    assertEquals(33, p1.getAge().intValue());

    // Check decrease button decreases age in GUI and Object
    e.gemButtonComp4LeftClick();
    e.gemButtonComp4LeftClick();
    assertEquals(31, e.getGemNumberInputComp2Value());
    assertEquals(31, p1.getAge().intValue());
  }

  /**
   * Valid changes in one client propagates to other clients
   */
  @Test
  public void observePersonGUITest() {
    FlensburgTestContext backendContext = FlensburgTestContext.fromCurrentContext();
    // a - address, aGemId - gem id of address
    long aGemId = 1001L;
    // p - person, pGemId - gem id of person
    long pGemId = 1002L;

    // simulate two different clients, which communicate with the same server
    FlensburgTestContext clientContext1 = new FlensburgManagerClientImplBuilder()
        .backendContext(backendContext).build().get();
    FlensburgTestContext clientContext2 = new FlensburgManagerClientImplBuilder()
        .backendContext(backendContext).build().get();

    // working with client 1
    clientContext1.activate();
    // create initial person with an address
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
    // page opened by a user on client 1
    PersonEdit e1 = new PersonEdit(p1);

    // working with client 2
    clientContext2.activate();
    // check that person is also available for client 2
    Optional<Person> pClient2Opt = FlensburgManager.personOpt(pGemId);
    assertTrue(pClient2Opt.isPresent());
    Person p2 = pClient2Opt.get();
    // page opened by a user on client 2
    PersonEdit e2 = new PersonEdit(p2);
    // TODO AGe: strategy on input fields update, check current object/attribute status

    clientContext1.activate();
    // check setting the age in GUI of client 1 changes the input value for GUI of client 2
    e1.setGemNumberInputComp2Value(33);
    assertEquals(33, e2.getGemNumberInputComp2Value());

    clientContext2.activate();
    // check decrease button click in GUI of client 2 decreases age in GUI of client 1
    e2.gemButtonComp4LeftClick();
    e2.gemButtonComp4LeftClick();
    assertEquals(31, e1.getGemNumberInputComp2Value());
  }

  /**
   * Error (invalid state) triggered on server will be shown in the client
   * May happen when client requires calculation on the server
   */
  @Test
  public void observeServerToClientErrorTest() {
    Log.initDEBUG();

    // create initial person with an address
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

    // simulate client which will communicate with server
    FlensburgTestContext clientContext1 = new FlensburgManagerClientImplBuilder()
        .backendContext(backendContext)
        .build()
        .get();

    // work on client
    clientContext1.activate();
    Person p1001C1 = FlensburgManager.getPersonSure(gemId);
    PersonEdit e1 = new PersonEdit(p1001C1);

    // no error initially in the GUI of client
    assertEquals("", e1.getGemNumberInputComp2ValueError());
    // work on server
    backendContext.activate();
    Person p1001S = FlensburgManager.getPersonSure(gemId);
    // set invalid age
    p1001S.setAge(15);

    // error is shown in GUI of client
    assertEquals("Age 15 of Person with gemId 1001 is invalid!",
        e1.getGemNumberInputComp2ValueError());
  }

  /**
   * Error (invalid state) triggered on client will be not be propagated to server
   */
  @Test
  public void observeClientToServerErrorTest() {
    Log.initDEBUG();

    FlensburgTestContext backendContext = FlensburgTestContext.fromCurrentContext();
    // create initial person with an address
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

    // add error collector for server to track errors
    List<String> serverErrorMsgs = new ArrayList<>();
    PersonErrorCollector.addErrorObserver(new PersonErrorObserver() {
      @Override
      public void notifyAgeInvalid(Person person, String msg) {
        serverErrorMsgs.add(msg);
      }
    });

    // simulate client which will communicate with server
    FlensburgTestContext clientContext1 = new FlensburgManagerClientImplBuilder()
        .backendContext(backendContext)
        .build()
        .get();

    // work on client
    clientContext1.activate();
    Person p1001C1 = FlensburgManager.getPersonSure(gemId);
    PersonEdit e1 = new PersonEdit(p1001C1);
    // put invalid value into input field
    e1.setGemNumberInputComp2Value(15);

    // error check happens in client and shows error
    assertEquals("Age 15 of Person with gemId 1001 is invalid!",
        e1.getGemNumberInputComp2ValueError());

    // work on server
    backendContext.activate();
    Person p1001S = FlensburgManager.getPersonSure(gemId);
    // invalid value is not propagated from client to server
    assertEquals(24, p1001S.getAge());
    // neither do errors
    assertTrue(serverErrorMsgs.isEmpty());
  }

}
