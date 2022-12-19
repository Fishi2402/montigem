package hardy.database;

import hardy.FlensburgManager;
import hardy.Human;
import hardy.HumanBuilder;
import hardy.HumanDAO;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeleteTest extends AbstractDBTest{

  @Test
  public void testHumanDaoDelete() {
    long gemId = 1001;
    int age = 20;
    HumanBuilder builder = new AbstractDBTest.MockHumanBuilder(gemId);
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

    FlensburgManager.deleteHuman(h1001);
    assertTrue(FlensburgManager.humanOpt(h1001.getGemId()).isEmpty());

    HumanDAO dao = new HumanDAO();
    assertTrue(dao.selectHuman(h1001.getGemId()).isEmpty());
  }


  // TODO: What about cascading deletes?
  // TODO: testen ob auch links entfernt werden wenn ein objekt entfernt wird
}
