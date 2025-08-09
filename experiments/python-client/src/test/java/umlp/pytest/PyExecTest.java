package umlp.pytest;

import mydomain.MyDomainManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
//    assertEquals(Long.toString(secondB.get().getGemId()), handler.sendCommand("print(my_a.directB.gemId)").get());
  }


}
