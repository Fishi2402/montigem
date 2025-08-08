package umlp.pytest;

import mydomain.MyDomainManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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


}
