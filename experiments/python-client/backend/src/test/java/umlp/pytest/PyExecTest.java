package umlp.pytest;

import de.se_rwth.commons.logging.Log;
import mydomain.MyDomainManager;
import mydomain.MyEnum;
import mydomain.TestAssoc_1_A;
import mydomain.TestAssoc_1_B;
import mydomain.TestAssoc_1_BBuilder;
import mydomain.TestAssoc_2_A;
import mydomain.TestAssoc_2_BBuilder;
import mydomain.TestAssoc_2_CBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
            " b=new_b.gemId, directB=" + secondB.get().getGemId() + ", myStr=\"hello world\", strList=[]" +
            "))\nprint(my_a.gemId)").get());
    assertEquals(1, MyDomainManager.getAList().size());
    var my_a = MyDomainManager.getASure(2);

    assertEquals("hello world", my_a.getMyStr());
    assertEquals(new ArrayList<>(), my_a.getStrListList());
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
    Thread.sleep(500); // TODO await (retry for 2 seconds)
    assertEquals("test2", handler.sendCommand("print(my_a.myStr)").get());

    // strList
    assertEquals("", handler.sendCommand("await my_a.strList_add(0, \"test\")").get());
    assertEquals(List.of("test"), my_a.getStrListList());
    my_a.addStrList(1, "test_from_java");
    Thread.sleep(500); // TODO await (retry for 2 seconds)
    assertEquals("['test', 'test_from_java']", handler.sendCommand("print(my_a.strList)").get());
    my_a.removeStrList(0);
    assertEquals(List.of("test_from_java"), my_a.getStrListList());
    Thread.sleep(500); // TODO await (retry for 2 seconds)
    assertEquals("['test_from_java']", handler.sendCommand("print(my_a.strList)").get());
    assertEquals("", handler.sendCommand("await my_a.strList_rem(0)").get());
    assertEquals(List.of(), my_a.getStrListList());
    //
    // TODO: test strOpt
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

    // enum
    assertEquals("A", handler.sendCommand("print(my_a.enumState)").get());
    assertEquals("<enum 'MyEnum'>", handler.sendCommand("print(type(my_a.enumState))").get());
    assertEquals("", handler.sendCommand("import MyDomain.MyEnum").get());
    assertEquals("", handler.sendCommand("await my_a.setEnumState(MyDomain.MyEnum.MyEnum.B)").get());
    Thread.sleep(100);
    assertEquals(MyEnum.B, my_a.getEnumState());
    my_a.setEnumState(MyEnum.C);
    Thread.sleep(100);
    assertEquals("C", handler.sendCommand("print(my_a.enumState)").get());
    assertEquals("<enum 'MyEnum'>", handler.sendCommand("print(type(my_a.enumState))").get());
    // enum

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

    assertEquals("", handler.sendCommand("await a1.listB_rem(1)").get());
    assertEquals(1, a1.getListBList().size());
    a1.clearListBs();
    Thread.sleep(500);
    assertEquals("0", handler.sendCommand("print(len(a1.listB))").get());


  }


  @Test
  public void testDirAssocBiDirectional() throws Exception {
    TestAssoc_2_BBuilder b1;
    TestAssoc_2_CBuilder c1;
    var a1Builder = MyDomainManager.testAssoc_2_ABuilder()
//        .test((b1 = MyDomainManager.testAssoc_2_BBuilder()))
        .single((c1 = MyDomainManager.testAssoc_2_CBuilder()))
        ;


    System.err.println(a1Builder.isBuildable());


    TestAssoc_2_A a1 = a1Builder.build().get();

    // Fetch A & C
    assertEquals(Long.toString(a1.getGemId()), getAndStore("a1", "TestAssoc_2_A", a1.getGemId()));
    assertEquals(Long.toString(c1.getGemId()), getAndStore("c1", "TestAssoc_2_C", c1.getGemId()));

    // Ensure gemIds are correct
    assertEquals(Long.toString(a1.getGemId()), handler.sendCommand("print(a1.gemId)").get());
    assertEquals(Long.toString(c1.getGemId()), handler.sendCommand("print(c1.gemId)").get());

    assertEquals(c1.getGemId(), a1.getSingleGem().getGemId());
    assertEquals(Long.toString(c1.getGemId()), handler.sendCommand("print(a1.single.gemId)").get());
    assertEquals(a1.getGemId(), c1.getSingleAGem().getGemId());
    assertEquals(Long.toString(a1.getGemId()), handler.sendCommand("print(c1.singleA.gemId)").get());

    // opt (TestAssoc_2_D)
    var d1 = MyDomainManager.testAssoc_2_DBuilder().build().get();

    assertEquals(Long.toString(d1.getGemId()), getAndStore("d1", "TestAssoc_2_D", d1.getGemId()));
    assertEquals(Long.toString(d1.getGemId()), handler.sendCommand("print(d1.gemId)").get());

    d1.setOptA(a1);
    Thread.sleep(500);
    assertEquals(a1.getOptGem().getGemId(), d1.getGemId()); // correct on server?
    assertEquals(Long.toString(a1.getGemId()), handler.sendCommand("print(d1.optA.gemId)").get()); // correct on client?
    assertEquals(Long.toString(a1.getOptGem().getGemId()), handler.sendCommand("print(a1.opt.gemId)").get()); // correct on client?

    assertEquals("", handler.sendCommand("await d1.setOptA(-1)").get()); // check that the client is able to send the correct command
    assertEquals(-1, a1.getOptGem().getGemId());
    assertEquals(Long.toString(-1), handler.sendCommand("print(a1.opt.gemId)").get());
    assertEquals(Long.toString(-1), handler.sendCommand("print(d1.optA.gemId)").get());

    assertEquals("", handler.sendCommand("await d1.setOptA(" + a1.getGemId() +")").get());
    assertEquals(a1.getOptGem().getGemId(), d1.getGemId());
    assertEquals(Long.toString(a1.getOptGem().getGemId()), handler.sendCommand("print(a1.opt.gemId)").get());

    // And now: The reverse (because associations are finicky)
    assertEquals("", handler.sendCommand("await a1.setOpt(-1)").get()); // check that the client is able to send the correct command
    assertEquals(-1, a1.getOptGem().getGemId());
    assertEquals(-1, d1.getOptAGem().getGemId());
    assertEquals(Long.toString(-1), handler.sendCommand("print(a1.opt.gemId)").get());
    assertEquals(Long.toString(-1), handler.sendCommand("print(d1.optA.gemId)").get());

    assertEquals("", handler.sendCommand("await a1.setOpt(" + d1.getGemId() +")").get());
    assertEquals(d1.getOptAGem().getGemId(), a1.getGemId());
    assertEquals(Long.toString(d1.getOptAGem().getGemId()), handler.sendCommand("print(d1.optA.gemId)").get());


    // [*]   <-> TestAssoc_2_E [*];

    // TODO

  }

  protected String getAndStore(String name, String type, long gemId)
      throws IOException, ExecutionException, InterruptedException {
    return handler.sendCommand(
        String.format("%1$s = await my_domain_manager.get(MyDomain.%2$s.%2$s, %3$s)\nprint(%1$s.gemId)", name, type, gemId) ).get();
  }
}
