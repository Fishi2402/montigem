/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp.UMLPMill;

import java.util.Optional;

public class ReplaceKeywordTest {

  @BeforeAll
  public static void init() {
    Log.enableFailQuick(false);
    UMLPMill.init();
  }

  @BeforeEach
  public void prepare() {
    Log.clearFindings();
  }

  @Test
  public void testOriginal() throws Exception{
    Optional<ASTCDDefinition> opt = UMLPMill.parser().parse_StringCDDefinition("classdiagram MyName { }");
    Assertions.assertTrue(opt.isPresent());
    Assertions.assertEquals(0, Log.getErrorCount());

    opt = CD4CodeMill.parser().parse_StringCDDefinition("classdiagram MyName { }");
    Assertions.assertTrue(opt.isPresent());
    Assertions.assertEquals(0, Log.getErrorCount());

    opt = CD4AnalysisMill.parser().parse_StringCDDefinition("classdiagram MyName { }");
    Assertions.assertTrue(opt.isPresent());
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testReplaceKeywordParsing() throws Exception{
    Optional<ASTCDDefinition> opt = UMLPMill.parser().parse_StringCDDefinition("umlp MyName { }");
    Assertions.assertTrue(opt.isPresent());
    Assertions.assertEquals(0, Log.getErrorCount());

    opt = CD4CodeMill.parser().parse_StringCDDefinition("umlp MyName { }");
    Assertions.assertTrue(opt.isPresent());
    Assertions.assertEquals(0, Log.getErrorCount());

    // No CD4AnalysisParserForUMLP exists => this will fail
//    opt = CD4AnalysisMill.parser().parse_StringCDDefinition("umlp MyName { }");
//    Assertions.assertTrue(opt.isPresent());
//    Assertions.assertEquals(0, Log.getErrorCount());
  }
}
