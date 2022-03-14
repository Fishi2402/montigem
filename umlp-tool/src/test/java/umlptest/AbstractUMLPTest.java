/* (c) https://github.com/MontiCore/monticore */

package umlptest;

import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import umlp.UMLPMill;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp._parser.UMLPParser;
import umlp._symboltable.IUMLPArtifactScope;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;
import static umlp.UMLPToolOptions.HWC_PATH;
import static umlp.UMLPToolOptions.OUTPUT;

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
      return ast.orElse(null);
    }
    catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    return null;
  }

  public IUMLPArtifactScope createSymTab(ASTUMLPCompilationUnit ast) {
    ASTMCQualifiedName pck = ast.isPresentMCPackageDeclaration()
        ? ast.getMCPackageDeclaration().getMCQualifiedName()
        : MCBasicTypesMill.mCQualifiedNameBuilder().build();
        IUMLPArtifactScope as = UMLPMill.scopesGenitorDelegator().createFromAST(ast);
    CD4AnalysisSymbolTableCompleter c = new CD4AnalysisSymbolTableCompleter(
        ast.getMCImportStatementList(), pck);
    ast.accept(c.getTraverser());
    return as;
  }

  public static void assertNoErrors() {
    if (Log.getErrorCount() > 0) {
      System.err.println("Expected no errors, but got: ");
      for (Finding f : Log.getFindings()) {
        if (f.isError()) {
          System.err.println(f.toString());
        }
      }
      fail();
    }
  }

  public static void assertErrors(String... errorCodes) {
    assertEquals("Expected and actual number of errors differ!",
        errorCodes.length, Log.getErrorCount());

    for (String expectedErrorCode : errorCodes) {
      boolean foundError = false;
      for (Finding f : Log.getFindings()) {
        if (f.isError() && f.getMsg().startsWith(expectedErrorCode)) {
          foundError = true;
          break;
        }
      }
      if (!foundError) {
        fail("Expected to find an error with the error code '" +
            expectedErrorCode + "', but the error did not occur!");
      }
    }
  }

  public static GeneratorSetup getGenSetup(){
    GeneratorSetup genSetup = new GeneratorSetup();

    // set output directory
    File outDir = new File("target/generated-test-sources");
    genSetup.setOutputDirectory(outDir);

    // set hwc directories
    MCPath hwcPath = new MCPath("src/test/java/");
    genSetup.setHandcodedPath(hwcPath);
    return genSetup;
  }
}
