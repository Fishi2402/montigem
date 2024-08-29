package umlp.cli;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import umlp.AbstractUMLPTest;
import umlp.UMLPTool;

import java.io.File;

public class CLICommandsTest extends AbstractUMLPTest {

  protected final static String TEST_PATH = "src/test/resources/de/monticore/";

  @Test
  public void testMergeCommand() {
    final String cd1 = TEST_PATH + "Person/A.umlp";
    final String cd2 = TEST_PATH + "Person/B.umlp";
    final String cd3 = TEST_PATH + "Person/C.umlp";
    final String out = "target/generated/multi-merge-test/Person/";
    UMLPTool.main(new String[] {"-i", cd1, "-mrg", cd2, cd3, "-pp", out + "Persons.umlp"});

    File outF = new File(out + "Persons.umlp");
    Assertions.assertTrue(outF.exists());
    Assertions.assertEquals(0, Log.getFindingsCount());
  }
}
