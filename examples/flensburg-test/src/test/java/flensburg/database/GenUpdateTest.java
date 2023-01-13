/* (c) https://github.com/MontiCore/monticore */
package flensburg.database;

import flensburg.*;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GenUpdateTest extends GenAbstractDBTest {

  @Test
  public void testHumanDaoSetAge() {
    long gemId = 1001;
    int age = 20;
    HumanBuilder builder = new MockHumanBuilder(gemId);
    builder.age(age);
    Human h1001 = builder.build().get();

    HumanDAO dao = new HumanDAO();
    dao.insertHuman(h1001);

    int changedAge = 21;
    dao.updateAge(gemId, changedAge);

    Optional<Human> h1001CopyOpt = dao.selectHuman(h1001.getGemId());
    assertTrue(h1001CopyOpt.isPresent());

    Human h1001Copy = h1001CopyOpt.get();
    assertNotSame(h1001, h1001Copy);

    assertEquals(h1001.getGemId(), h1001Copy.getGemId());
    assertEquals(changedAge, h1001Copy.getAge());
  }

  @Test
  public void testHumanLoadFromDbAndSetAge(){
    long gemId = 1002;
    int age = 20;
    HumanBuilder builder = new MockHumanBuilder(gemId);
    builder.age(age);
    Human h1001db = builder.build().get();

    HumanDAO dao = new HumanDAO();
    dao.insertHuman(h1001db);

    Human h1001 = FlensburgManager.getHumanSure(gemId);
    assertNotSame(h1001db, h1001);
    assertEquals(gemId, h1001.getGemId());
    assertEquals(age, h1001.getAge());
  }

  @Test
  public void testHumanSetAge(){
    int age = 20;
    Human h = FlensburgManager.humanBuilder().age(age).build().get();
    int changedAge = 21;
    h.setAge(changedAge);

    HumanDAO dao = new HumanDAO();
    Optional<Human> hCopyOpt = dao.selectHuman(h.getGemId());
    assertTrue(hCopyOpt.isPresent());
    Human hCopy = hCopyOpt.get();

    assertNotSame(h, hCopy);
    assertEquals(changedAge, h.getAge());
    assertEquals(h.getGemId(), hCopy.getGemId());
  }

  @Test
  public void testPersonAddVisited(){
    Address a1 = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    PersonBuilder pb = FlensburgManager.personBuilder().age(20).nickname("John").mainResidence(a1);
    Person p = pb.build().get();

    PersonDAO personDAO = new PersonDAO();

    // Initially empty
    {
      Optional<Person> pCopyOpt = personDAO.selectPerson(p.getGemId());
      assertTrue(pCopyOpt.isPresent());

      Person pCopy = pCopyOpt.get();
      assertNotSame(p, pCopy);

      assertEquals(0, pCopy.sizeVisiteds());
    }

    // Add visited
    p.addVisited(a1);

    {
      Optional<Person> pCopyOpt = personDAO.selectPerson(p.getGemId());
      assertTrue(pCopyOpt.isPresent());

      Person pCopy = pCopyOpt.get();
      assertNotSame(p, pCopy);

      assertEquals(1, pCopy.sizeVisiteds());
    }
  }

  @Test
  public void testAddressAddVisitor(){
    Address a1 = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Address a2 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    PersonBuilder pb = FlensburgManager.personBuilder().age(20).nickname("John").mainResidence(a1);
    Person p = pb.build().get();


    AddressDAO addressDAO = new AddressDAO();

    // Initially empty
    {
      Optional<Address> a2CopyOpt = addressDAO.selectAddress(a2.getGemId());
      assertTrue(a2CopyOpt.isPresent());

      Address a2Copy = a2CopyOpt.get();
      assertNotSame(a2, a2Copy);

      assertEquals(0, a2Copy.sizeVisitors());
    }

    // Add visited
    a2.addVisitor(p);

    {
      Optional<Address> a2CopyOpt = addressDAO.selectAddress(a2.getGemId());
      assertTrue(a2CopyOpt.isPresent());

      Address a2Copy = a2CopyOpt.get();
      assertNotSame(a2, a2Copy);

      assertEquals(1, a2Copy.sizeVisitors());
    }
  }
}
