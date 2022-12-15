package hardy.database;

import hardy.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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

  // TODO: Delete


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
