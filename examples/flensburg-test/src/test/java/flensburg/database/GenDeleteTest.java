/* (c) https://github.com/MontiCore/monticore */
package flensburg.database;

import flensburg.*;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenDeleteTest extends GenAbstractDBTest {

  @Test
  public void testHumanDaoDelete() {
    long gemId = 1001;
    int age = 20;
    HumanBuilder builder = new GenAbstractDBTest.MockHumanBuilder(gemId);
    builder.age(age);
    Human h1001 = builder.build().get();

    HumanDAO dao = new HumanDAO();
    dao.insertHuman(h1001);

    dao.deleteHuman(gemId);

    Optional<Human> h1001CopyOpt = dao.selectHuman(h1001.getGemId());
    assertTrue(h1001CopyOpt.isEmpty());
  }

  @Test
  public void testDeleteHumanWithDao(){
    long gemId = 1002;
    int age = 20;
    HumanBuilder builder = new MockHumanBuilder(gemId);
    builder.age(age);
    Human h1001db = builder.build().get();

    HumanDAO dao = new HumanDAO();

    dao.insertHuman(h1001db);
    assertTrue(dao.selectHuman(gemId).isPresent());

    dao.deleteHuman(gemId);
    assertTrue(dao.selectHuman(gemId).isEmpty());
  }

  @Test
  public void testDeleteHumanWithManager(){
    int age = 20;
    HumanBuilder builder = FlensburgManager.humanBuilder();
    builder.age(age);
    Human h1001 = builder.build().get();

    FlensburgManager.deleteHuman(h1001.getGemId());
    assertTrue(FlensburgManager.humanOpt(h1001.getGemId()).isEmpty());

    HumanDAO dao = new HumanDAO();
    assertTrue(dao.selectHuman(h1001.getGemId()).isEmpty());
  }

  @Test
  public void testAssocDeleteNavigableDirection() {

    Address address = FlensburgManager.addressBuilder().city("Koeln").build().get();
    Address uninterestingAddress = FlensburgManager.addressBuilder().city("Aachen").build().get();
    PersonBuilder personBuilder = FlensburgManager.personBuilder().age(33).nickname("James Bond").mainResidence(uninterestingAddress);

    Optional<Person> jamesBond = personBuilder.build();

    address.addVisitor(jamesBond.get());

    long personId = jamesBond.get().getGemId();
    long addressId = address.getGemId();

    AddressDAO addressDAO = new AddressDAO();
    assertTrue(addressDAO.selectAddress(addressId).isPresent());

    PersonDAO personDAO = new PersonDAO();
    assertTrue(personDAO.selectPerson(personId).isPresent());

    AssocPerson_person_mainResidence_AddressDAO assocPerson_person_mainResidence_addressDAO = new AssocPerson_person_mainResidence_AddressDAO();
    assertFalse(assocPerson_person_mainResidence_addressDAO.selectMainResidenceIdForPerson(personId).isEmpty());

    AssocVisitsDAO assocVisitsDAO = new AssocVisitsDAO();
    assertFalse(assocVisitsDAO.selectVisitedIdsForVisitor(personId).isEmpty());
    assertFalse(assocVisitsDAO.selectVisitorIdsForVisited(addressId).isEmpty());

    addressDAO.deleteAddress(addressId);

    assertTrue(assocVisitsDAO.selectVisitedIdsForVisitor(personId).isEmpty());
    assertTrue(assocVisitsDAO.selectVisitorIdsForVisited(addressId).isEmpty());
  }

  @Test
  public void testAssocDeletesNotNavigableDirection() {
    // TODO do we need this?
  }

  // TODO: What about cascading deletes?
  // TODO: testen ob auch links entfernt werden wenn ein objekt entfernt wird
}
