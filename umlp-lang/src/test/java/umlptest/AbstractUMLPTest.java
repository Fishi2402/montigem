/* (c) https://github.com/MontiCore/monticore */

package umlptest;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import umlp.UMLPMill;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp._parser.UMLPParser;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class AbstractUMLPTest {

  protected UMLPParser parser = new UMLPParser();

  @Before
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    UMLPMill.reset();
    UMLPMill.init();
  }

  public ASTUMLPCompilationUnit parse(String model) {
    return parse("src/test/resources", model);
  }

  public ASTUMLPCompilationUnit parse(String testPath, String model) {
    Path m = Paths.get(testPath, model);
    assertTrue(m.toFile().exists());
    try {
      Optional<ASTUMLPCompilationUnit> ast = parser.parse(m.toString());
      assertTrue(ast.isPresent());
      return ast.get();
    }
    catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    return null;
  }
}
