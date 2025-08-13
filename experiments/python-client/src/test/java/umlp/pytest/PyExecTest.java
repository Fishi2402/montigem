package umlp.pytest;

import mydomain.MyDomainManager;
import mydomain.TestAssoc_1_A;
import mydomain.TestAssoc_1_B;
import mydomain.TestAssoc_1_BBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PyExecTest extends AbstractPyTest {


  @Test
  void testPyTest() throws IOException, ExecutionException, InterruptedException {
    assertEquals("a", handler.sendCommand("print('a')").get());
    assertEquals("ba", handler.sendCommand("print('ba')").get());

    assertEquals("ca\ncb", handler.sendCommand("print('ca')\nprint('cb')").get());
  }

  @Test
  public void testB() throws IOException, ExecutionException, InterruptedException {
    assertEquals(0, MyDomainManager.getBList().size());
    assertEquals("0", handler.sendCommand(
        "print(len((await my_domain_manager.list_filtered(MyDomain.B.B))['list']))").get());
    assertEquals("gemId=0", handler.sendCommand(
            "new_b = await my_domain_manager.create(MyDomain.B.B(gemId=await my_domain_manager.get_free_gem_id() ))\nprint(new_b)")
        .get());
    assertEquals(1, MyDomainManager.getBList().size());
    // Create a new object
    MyDomainManager.bBuilder().build();
    assertEquals(2, MyDomainManager.getBList().size());
    assertEquals("2", handler.sendCommand(
        "print(len((await my_domain_manager.list_filtered(MyDomain.B.B))['list']))").get());

  }


  @Test
  public void testA() throws IOException, ExecutionException, InterruptedException {
    assertEquals(0, MyDomainManager.getBList().size());
    assertEquals(0, MyDomainManager.getAList().size());
    // create a B
    int new_b_gem_id = 0;
    assertEquals(Integer.toString(new_b_gem_id), handler.sendCommand(
            "new_b = await my_domain_manager.create(MyDomain.B.B(gemId=await my_domain_manager.get_free_gem_id() ))\nprint(new_b.gemId)")
        .get());
    assertEquals(1, MyDomainManager.getBList().size());
    var secondB = MyDomainManager.bBuilder().build();
    assertEquals(2, MyDomainManager.getBList().size());
    // Create a new object
    assertEquals(2, MyDomainManager.getBList().size());
    assertEquals("2", handler.sendCommand(
        "my_a = await my_domain_manager.create(MyDomain.A.A(\n" +
            " gemId=await my_domain_manager.get_free_gem_id(),\n" +
            " b=new_b.gemId, directB=" + secondB.get().getGemId() + ", myStr=\"hello world\"" +
            "))\nprint(my_a.gemId)").get());
    assertEquals(1, MyDomainManager.getAList().size());
    var my_a = MyDomainManager.getASure(2);

    assertEquals("hello world", my_a.getMyStr());
    assertEquals(Optional.empty(), my_a.getStrOpt());
    assertEquals(secondB.get().getGemId(), my_a.getDirectBGem().getGemId());
    assertEquals(42, my_a.getI());
    assertEquals(42.42, my_a.getD());
    assertEquals(0, my_a.getB().getGemId());
    assertEquals(true, my_a.isMyBool());
    // Test Updating
    assertEquals("", handler.sendCommand("await my_a.setMyStr(\"test\")").get());
    assertEquals("test", my_a.getMyStr());
    my_a.setMyStr("test2");
    System.out.println("wait for test2");
    Thread.sleep(500); // TODO await (retry for 2 seconds)
    System.out.println("waited for test2");
    assertEquals("test2", handler.sendCommand("print(my_a.myStr)").get());
    //
    // TODO: strOpt
    //
    assertEquals("", handler.sendCommand("await my_a.setI(3)").get());
    System.out.println(my_a.getGemId() + "//" + my_a.getI());
    assertEquals(3, my_a.getI());
    my_a.setI(3);
    Thread.sleep(500); // TODO await (retry for 2 seconds)
    assertEquals("3", handler.sendCommand("print(my_a.i)").get());

    //

    assertEquals("", handler.sendCommand("await my_a.setDirectB(new_b.gemId)").get());
    assertEquals(Integer.toString(new_b_gem_id), handler.sendCommand("print(new_b.gemId)").get());
    assertEquals(new_b_gem_id, my_a.getDirectBGem().getGemId());
    my_a.setDirectB(secondB.get());
    Thread.sleep(500);

    // TODO
    assertEquals(Long.toString(secondB.get().getGemId()), handler.sendCommand("print(my_a.directB.gemId)").get());
  }

  @Test
  public void testDirAssoc() throws Exception {
    TestAssoc_1_BBuilder b1, b2;
    TestAssoc_1_A a1 = MyDomainManager.testAssoc_1_ABuilder()
        .testAssoc_1_B((b1 = MyDomainManager.testAssoc_1_BBuilder()))
        .singleB((b2 = MyDomainManager.testAssoc_1_BBuilder()))
        .build().get();
    b1.build().get();
    b2.build().get();

    assertEquals(Long.toString(a1.getGemId()), getAndStore("a1", "TestAssoc_1_A", a1.getGemId()));
    assertEquals(Long.toString(b1.getGemId()), getAndStore("b1", "TestAssoc_1_B", b1.getGemId()));
    assertEquals(Long.toString(b2.getGemId()), getAndStore("b2", "TestAssoc_1_B", b2.getGemId()));

//     test optional gem
    assertEquals("-1", handler.sendCommand("print(a1.optB.gemId)").get());
    a1.setOptB(b2);
    Thread.sleep(500);
    assertEquals(Long.toString(b2.getGemId()), handler.sendCommand("print(a1.optB.gemId)").get());
    // TODO: is present?
    a1.setOptBAbsent();
    Thread.sleep(500);
    assertEquals("-1", handler.sendCommand("print(a1.optB.gemId)").get());
    //
    assertEquals("", handler.sendCommand("await a1.setOptB( " + b2.getGemId() + ")").get());
    assertEquals(b2.getGemId(), a1.getOptBGem().getGemId());
    assertEquals("", handler.sendCommand("await a1.setOptB(-1)").get());
    assertFalse(a1.isPresentOptB());


    assertEquals("0", handler.sendCommand("print(len(a1.listB))").get());
    a1.addListB(b1);
    Thread.sleep(500);
    assertEquals("1", handler.sendCommand("print(len(a1.listB))").get());

    assertEquals("", handler.sendCommand("await a1.listB_add(1, " +b2.getGemId()+")").get());
    assertEquals(2, a1.getListBList().size());

  }

  protected String getAndStore(String name, String type, long gemId)
      throws IOException, ExecutionException, InterruptedException {
    return handler.sendCommand(
        String.format("%1$s = await my_domain_manager.get(MyDomain.%2$s.%2$s, %3$s)\nprint(%1$s.gemId)", name, type, gemId) ).get();
  }
}
