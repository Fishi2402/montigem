package guiTest;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.util.CDMergeAfterParseTrafo;
import de.monticore.generating.GeneratorSetup;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._parser.GUIDSLParser;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import umlp.UMLPMill;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._visitor.UMLPTraverser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractGUITest {

  public static ASTGUICompilationUnit parseGUI(String model) {
    return parseGUI("src/test/resources/", model);
  }

  public static ASTGUICompilationUnit parseGUI(String resources, String model) {
    try {
      GUIDSLParser parser = new GUIDSLParser();
      Optional<ASTGUICompilationUnit> optAst = parser.parse(resources + model);

      if (!parser.hasErrors() && optAst.isPresent()) {
        return optAst.get();
      }
      Log.error("0xA1050x72114 Model could not be parsed.");
    }
    catch (NullPointerException | java.io.IOException e) {
      Log.error("0xA1051x37376 Failed to parse " + model, e);
    }
    // should never be reached (unless failquick is off)
    return null;
  }

  public static void assertNoErrors() {
    if (Log.getErrorCount() > 0) {
      System.err.println("Expected no errors, but got: ");
      for (Finding f : Log.getFindings()) {
        if (f.isError()) {
          System.err.println(f);
        }
      }
      fail();
    }
  }

  public static void assertErrors(String... errorCodes) {
    assertEquals(errorCodes.length, Log.getErrorCount(),
        "Expected and actual number of errors differ!");

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

  public static GeneratorSetup getGenSetup() {
    GeneratorSetup genSetup = new GeneratorSetup();

    // set output directory
    File outDir = new File("target/generated-test-sources");
    genSetup.setOutputDirectory(outDir);

    // set hwc directories
    MCPath hwcPath = new MCPath("src/test/java/");
    genSetup.setHandcodedPath(hwcPath);
    return genSetup;
  }

  @BeforeEach
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    UMLPMill.reset();
    UMLPMill.init();
    BuiltInTypes.addBuiltInTypes(UMLPMill.globalScope());
  }

  public ASTCDCompilationUnit parse(String model) {
    return parse("src/test/resources", model);
  }

  public ASTCDCompilationUnit parse(String testPath, String model) {
    Path m = Paths.get(testPath, model);
    assertTrue(m.toFile().exists());
    try {
      Optional<ASTCDCompilationUnit> ast = UMLPMill.parser().parse(m.toString());
      assertTrue(ast.isPresent());
      return ast.get();
    }
    catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    return null;
  }

  public IUMLPArtifactScope createSymTab(ASTCDCompilationUnit ast) {
    ASTMCQualifiedName pck = ast.isPresentMCPackageDeclaration()
        ? ast.getMCPackageDeclaration().getMCQualifiedName()
        : MCBasicTypesMill.mCQualifiedNameBuilder().build();
    IUMLPArtifactScope as = UMLPMill.scopesGenitorDelegator().createFromAST(ast);
    CD4CodeSymbolTableCompleter c = new CD4CodeSymbolTableCompleter(ast);
    ast.accept(c.getTraverser());
    return as;
  }

  protected void runAfterParseTrafo(ASTCDCompilationUnit node) {
    UMLPTraverser trav = UMLPMill.traverser();
    CD4CodeAfterParseTrafo.init(new CDAfterParseHelper(), trav);
    node.accept(trav);
  }

  protected void runAfterParseTrafoForMerge(ASTCDCompilationUnit node) {
    UMLPTraverser trav = UMLPMill.traverser();
    CDMergeAfterParseTrafo.init(new CDAfterParseHelper(), trav);
    node.accept(trav);
  }
}
