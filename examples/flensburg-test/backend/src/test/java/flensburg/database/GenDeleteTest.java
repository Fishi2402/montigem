/* (c) https://github.com/MontiCore/monticore */
package flensburg.database;

import hardy.flensburg.Address;
import hardy.flensburg.AddressDAO;
import hardy.flensburg.AddressDAOInterface;
import hardy.flensburg.AssocPerson_person_mainResidence_AddressDAO;
import hardy.flensburg.AssocPerson_person_mainResidence_AddressDAOInterface;
import hardy.flensburg.AssocVisitsDAO;
import hardy.flensburg.AssocVisitsDAOInterface;
import hardy.flensburg.FlensburgDAOFactory;
import hardy.flensburg.FlensburgManager;
import hardy.flensburg.Human;
import hardy.flensburg.HumanBuilder;
import hardy.flensburg.HumanDAO;
import hardy.flensburg.HumanDAOInterface;
import hardy.flensburg.Person;
import hardy.flensburg.PersonBuilder;
import hardy.flensburg.PersonDAO;
import hardy.flensburg.PersonDAOInterface;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class GenDeleteTest extends GenAbstractDBTest {

  @DisplayName("Neo4J Delete Tests")
  public static class Neo4JTestSuite extends GenDeleteTest {
    Neo4JTestSuite() {
      super(DatabaseConnectionType.NEO4J);
    }
  }

  @DisplayName("SQL Delete Tests")
  public static class SQLTestSuite extends GenDeleteTest {
    SQLTestSuite() {
      super(DatabaseConnectionType.SQL);
    }
  }

  public GenDeleteTest(DatabaseConnectionType dbConnectionType) {
    super(dbConnectionType);
  }

  @Test
  public void testHumanDaoDelete() {
    long gemId = 1001;
    int age = 20;
    HumanBuilder builder = new MockHumanBuilder(gemId);
    builder.age(age);
    Human h1001 = builder.build().get();

    HumanDAOInterface dao = FlensburgDAOFactory.getHumanDAO();
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

    HumanDAOInterface dao = FlensburgDAOFactory.getHumanDAO();

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

    HumanDAOInterface dao = FlensburgDAOFactory.getHumanDAO();
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

    AddressDAOInterface addressDAO = FlensburgDAOFactory.getAddressDAO();
    assertTrue(addressDAO.selectAddress(addressId).isPresent());

    PersonDAOInterface personDAO = FlensburgDAOFactory.getPersonDAO();
    assertTrue(personDAO.selectPerson(personId).isPresent());

    AssocPerson_person_mainResidence_AddressDAOInterface assocPerson_person_mainResidence_addressDAO = FlensburgDAOFactory.getAssocPerson_person_mainResidence_AddressDAO();
    assertFalse(assocPerson_person_mainResidence_addressDAO.selectMainResidenceIdForPerson(personId).isEmpty());

    AssocVisitsDAOInterface assocVisitsDAO = FlensburgDAOFactory.getAssocVisitsDAO();
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
