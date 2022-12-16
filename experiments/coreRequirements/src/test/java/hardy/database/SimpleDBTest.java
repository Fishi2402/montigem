package hardy.database;

import hardy.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// Note: see TestDatabaseAccessManager.openWebServer to inspect the in-memory db
public class SimpleDBTest {

  @BeforeEach
  public void init(){
    TestDatabaseAccessManager.init();
    FlensburgManagerImpl.init();
  }

  @AfterEach
  public void cleanup(){
    TestDatabaseAccessManager.close();
  }

  @Test
  public void testHumanDaoInsertAndSelect() {
    long gemId = 1001;
    int age = 20;
    HumanBuilder builder = new MockHumanBuilder(gemId);
    builder.age(age);
    Human h1001 = builder.build().get();

    HumanDAO dao = new HumanDAO();
    dao.insertHuman(h1001);

    Optional<Human> h1001CopyOpt = dao.selectHuman(h1001.getGemId());
    assertTrue(h1001CopyOpt.isPresent());

    Human h1001Copy = h1001CopyOpt.get();
    assertNotSame(h1001, h1001Copy);

    assertEquals(h1001.getGemId(), h1001Copy.getGemId());
    assertEquals(h1001.getAge(), h1001Copy.getAge());
  }

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
  public void testHumanDaoDelete() {
    long gemId = 1001;
    int age = 20;
    HumanBuilder builder = new MockHumanBuilder(gemId);
    builder.age(age);
    Human h1001 = builder.build().get();

    HumanDAO dao = new HumanDAO();
    dao.insertHuman(h1001);

    dao.deleteHuman(gemId);

    Optional<Human> h1001CopyOpt = dao.selectHuman(h1001.getGemId());
    assertTrue(h1001CopyOpt.isEmpty());
  }

  // Same tests but with HumanDbObserver
  @Test
  public void testHumanCreate(){
    int age = 20;
    Human h = FlensburgManager.humanBuilder().age(age).build().get();

    HumanDAO dao = new HumanDAO();
    Optional<Human> hCopyOpt = dao.selectHuman(h.getGemId());
    assertTrue(hCopyOpt.isPresent());
    Human hCopy = hCopyOpt.get();

    assertNotSame(h, hCopy);
    assertEquals(age, h.getAge());
    assertEquals(h.getGemId(), hCopy.getGemId());
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
  public void testHumanLoadFromDb(){
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

    FlensburgManager.deleteHuman(h1001);
    assertTrue(FlensburgManager.humanOpt(h1001.getGemId()).isEmpty());

    HumanDAO dao = new HumanDAO();
    assertTrue(dao.selectHuman(h1001.getGemId()).isEmpty());
  }

  @Test
  public void testMainResidence(){
    Address a1 = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Person p1 = FlensburgManager.personBuilder().age(20).nickname("John").mainResidence(a1).build().get();

    PersonDAO personDAO = new PersonDAO();

    Optional<Person> p1CopyOpt = personDAO.selectPerson(p1.getGemId());
    assertTrue(p1CopyOpt.isPresent());
    Person p1Copy = p1CopyOpt.get();

    assertNotSame(p1, p1Copy);
    assertEquals(p1.getGemId(), p1Copy.getGemId());
    assertEquals(p1.getAge(), p1Copy.getAge());
    assertEquals(p1.getNickname(), p1Copy.getNickname());
    assertEquals(p1.getMainResidenceGem().getGemId(), p1Copy.getMainResidenceGem().getGemId());

    assertSame(a1, p1Copy.getMainResidence());
  }

  @Test
  public void testCreatePersonWithVisited(){
    Address a1 = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Address a2 = FlensburgManager.addressBuilder().city("Aachen").build().get();
    PersonBuilder pb = FlensburgManager.personBuilder().age(20).nickname("John").mainResidence(a1);
    pb.addVisited(a1);
    pb.addVisited(a2);
    Person p = pb.build().get();

    PersonDAO personDAO = new PersonDAO();

    Optional<Person> pCopyOpt = personDAO.selectPerson(p.getGemId());
    assertTrue(pCopyOpt.isPresent());

    Person pCopy = pCopyOpt.get();
    assertNotSame(p, pCopy);

    assertEquals(2, pCopy.sizeVisited());
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

      assertEquals(0, pCopy.sizeVisited());
    }

    // Add visited
    p.addVisited(a1);

    {
      Optional<Person> pCopyOpt = personDAO.selectPerson(p.getGemId());
      assertTrue(pCopyOpt.isPresent());

      Person pCopy = pCopyOpt.get();
      assertNotSame(p, pCopy);

      assertEquals(1, pCopy.sizeVisited());
    }
  }


  @Test
  public void testCreateAddressWithVisitors(){
    Address a1 = FlensburgManager.addressBuilder().city("Flensburg").build().get();

    PersonBuilder pb1 = FlensburgManager.personBuilder().age(20).nickname("John").mainResidence(a1);
    Person p1 = pb1.build().get();

    PersonBuilder pb2 = FlensburgManager.personBuilder().age(25).nickname("Doe").mainResidence(a1);
    Person p2 = pb2.build().get();

    Address a2 = FlensburgManager.addressBuilder().city("Aachen").visitorGemListIds(List.of(p1.getGemId(), p2.getGemId())).build().get();

    AddressDAO addressDAO = new AddressDAO();

    Optional<Address> a2CopyOpt = addressDAO.selectAddress(a2.getGemId());
    assertTrue(a2CopyOpt.isPresent());

    Address a2Copy = a2CopyOpt.get();
    assertNotSame(a2, a2Copy);

    assertEquals(2, a2Copy.sizeVisitors());
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

  // TODO: same from the perspective of address


  private static class MockHumanBuilder extends HumanBuilder{
    // Accessible without FlensburgManager to test DAO without observers
    public MockHumanBuilder(long gemId) {
      super(gemId);
      // Dont automatically add to FlensburgManager on build
      // so we can test loading entities from db
      INTERNAL_deser_mode = true;
    }
  }
}
