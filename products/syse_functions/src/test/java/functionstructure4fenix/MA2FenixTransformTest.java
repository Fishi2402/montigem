package functionstructure4fenix;

import functionstructure4fenix.ma2fenix.MA2FenixTool;
import functionstructure4fenix.test_infrastructure.FunctionStructure4FenixDBTestSetup;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class MA2FenixTransformTest extends FunctionStructure4FenixDBTestSetup {
  public MA2FenixTransformTest(){
    super(0);
  }

  Path input1 = Paths.get("src/test/resources/ma2fenix/channelsplit");
  Path input2 = Paths.get("src/test/resources/ma2fenix/general");

  @Test
  public void Test1(){
    String[] args = new String[] {
        "-mp", input1.toAbsolutePath().toString(),
        "-main", "A"
    };
    MA2FenixTool.main(args);
    Function a = FunctionStructure4FenixManagerImpl.getFunctionSure(1);
    Function b1 = a.getSubFunction(0);
    Function c1 = b1.getSubFunction(0);
    System.out.println(a.getName());
    System.out.println(b1.getName());
    System.out.println(c1.getName());
    assertEquals(FunctionStructure4FenixManagerImpl.getChannelList().size(), 2);
    assertEquals(FunctionStructure4FenixManagerImpl.getDataList().size(), 1);
    assertEquals(FunctionStructure4FenixManagerImpl.getCategoryList().size(), 1);
    assertEquals(FunctionStructure4FenixManagerImpl.getTypeList().size(), 1);
    assertEquals("a", a.getName());
    assertEquals("b1", b1.getName());
    assertEquals("c1", c1.getName());
    assertEquals(a.getOutChannel("b"), a.getOutChannel("d"));
    assertEquals(a.getOutChannel("d"), c1.getOutChannel("b"));
    assertNotEquals(a.getInChannel("a"), a.getOutChannel("b"));
  }

  @Test
  public void Test2(){
    String[] args = new String[] {
        "-mp", input2.toAbsolutePath().toString(),
        "-main", "L"
    };
    MA2FenixTool.main(args);
    Function l = FunctionStructure4FenixManagerImpl.getFunctionSure(1);
    Function m1 = l.getSubFunction(0);
    Function n1 = m1.getSubFunction(0);
    Function n2 = m1.getSubFunction(1);
    assertEquals(5, FunctionStructure4FenixManagerImpl.getChannelList().size());
    assertEquals(1, FunctionStructure4FenixManagerImpl.getDataList().size());
    assertEquals(2, FunctionStructure4FenixManagerImpl.getCategoryList().size());
    assertEquals(2, FunctionStructure4FenixManagerImpl.getTypeList().size());
    assertEquals("l", l.getName());
    assertEquals("m1", m1.getName());
    assertEquals("n1", n1.getName());
    assertEquals("n2", n2.getName());
    assertEquals(l.getOutChannel("b"), m1.getOutChannel("d"));
    assertEquals(n1.getOutChannel("d"), n2.getInChannel("c"));
  }
}
