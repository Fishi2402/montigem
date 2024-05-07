package functionstructure4fenix_automaton;

import functionstructure4fenix.Function;
import functionstructure4fenix.FunctionStructure4Fenix4FunctionStructure4Fenix_AutomatonManagerImpl;
import functionstructure4fenix.FunctionStructure4FenixManager;
import functionstructure4fenix_automaton.ma2fenix.automaton.MA2FenixAutomatonTool;
import functionstructure4fenix_automaton.test_infrastructure.FunctionStructure4Fenix_AutomatonSQLDBTestSetup;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class MA2FenixAutTransformTest extends FunctionStructure4Fenix_AutomatonSQLDBTestSetup {
  public MA2FenixAutTransformTest(){
    super(0);
  }

  Path input1 = Paths.get("src/test/resources/ma2fenix/test1");

  @Test
  public void Test1(){
    String[] args = new String[] {
        "-mp", input1.toAbsolutePath().toString(),
        "-main", "A"
    };
    FunctionStructure4Fenix4FunctionStructure4Fenix_AutomatonManagerImpl manager =
        new FunctionStructure4Fenix4FunctionStructure4Fenix_AutomatonManagerImpl();
    manager.init();
    FunctionStructure4FenixManager.initMe(manager);

    MA2FenixAutomatonTool.main(args);

    Function a = FunctionStructure4Fenix_AutomatonManager.getFunctionAutList().get(0);
    Automaton aut = FunctionStructure4Fenix_AutomatonManager.getAutomatonList().get(0);
    Function b1 = a.getSubFunction(0);
    Function c1 = b1.getSubFunction(0);
    assertEquals(2, aut.getStates());
    assertEquals("a", a.getName());
    assertEquals("b1", b1.getName());
    assertEquals("c1", c1.getName());
    assertEquals(a.getOutChannel("b"), a.getOutChannel("d"));
    assertEquals(a.getOutChannel("d"), c1.getOutChannel("b"));
    assertNotEquals(a.getInChannel("a"), a.getOutChannel("b"));
  }
}
