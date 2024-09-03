package umlp.cli;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import umlp.AbstractUMLPTest;
import umlp.UMLPTool;

import java.io.File;
import java.nio.file.Path;

public class CLICommandsTest extends AbstractUMLPTest {

  protected final static String TEST_PATH = "src/test/resources/de/monticore/";

  @Test
  public void testPrettyPrintAndParse(@TempDir Path out){

    // Parse and pretty-print the umlp model
    final String cd = TEST_PATH + "AssocTest.umlp";
    UMLPTool.main(new String[] {"-i", cd, "-pp", out + "AssocTest/AssocTest.umlp"});

    File outF = new File(out + "AssocTest/AssocTest.umlp");
    Assertions.assertTrue(outF.exists());
    Assertions.assertEquals(0, Log.getFindingsCount());

    // Reparse to check if the pretty-printing produces a syntactically sound model
    UMLPTool.main(new String[] {"-i", out + "AssocTest/AssocTest.umlp", "-pp",
        out + "AssocTest/AssocTest.umlp"});
    Assertions.assertEquals(0, Log.getFindingsCount());
  }

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
