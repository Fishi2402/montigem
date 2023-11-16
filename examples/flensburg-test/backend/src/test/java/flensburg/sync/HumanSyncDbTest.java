/* (c) https://github.com/MontiCore/monticore */
package flensburg.sync;

import hardy.flensburg.FlensburgManager;
import hardy.flensburg.HumanDAO;
import hardy.flensburg.test_infrastructure.FlensburgDBTestSetup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HumanSyncDbTest extends FlensburgDBTestSetup {
  public HumanSyncDbTest() {
    super(1);
  }

  @Test
  public void testFieldChangeIsSavedInDb(){
    getClients(0).activate();
    var h = FlensburgManager.humanBuilder().age(20).build().get();

    HumanDAO dao = new HumanDAO();
    var hDb1 = dao.selectHuman(h.getGemId()).get();
    assertEquals(h.getAge(), hDb1.getAge());

    h.setAge(21);
    var hDb2 = dao.selectHuman(h.getGemId()).get();
    assertEquals(h.getAge(), hDb2.getAge());
  }
}
