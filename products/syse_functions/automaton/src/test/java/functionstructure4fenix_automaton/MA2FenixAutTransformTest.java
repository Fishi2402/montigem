package functionstructure4fenix_automaton;

import de.se_rwth.commons.logging.Log;
import functionstructure4fenix.Function;
import functionstructure4fenix.FunctionStructure4Fenix4FunctionStructure4Fenix_AutomatonManagerImpl;
import functionstructure4fenix.FunctionStructure4FenixDAOFactory;
import functionstructure4fenix.FunctionStructure4FenixDbObserver;
import functionstructure4fenix.FunctionStructure4FenixManager;
import functionstructure4fenix.FunctionStructure4FenixManagerClientImpl;
import functionstructure4fenix.FunctionStructure4FenixManagerImpl;
import functionstructure4fenix.FunctionStructure4FenixTestContext;
import functionstructure4fenix_automaton.ma2fenix.automaton.MA2FenixAutomatonTool;
import functionstructure4fenix_automaton.test_infrastructure.EnsureCorrectManagerFunctionStructure4Fenix_Automaton;
import functionstructure4fenix_automaton.test_infrastructure.FunctionStructure4Fenix_AutomatonDBTestSetup;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import umlp.backendrte.command.CommandDirectCommunication;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.TestClientCommandManager;
import umlp.backendrte.common.DatabaseAccessManager;
import umlp.backendrte.common.DatabaseAccessManager4SQL;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class MA2FenixAutTransformTest extends FunctionStructure4Fenix_AutomatonDBTestSetup {
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
