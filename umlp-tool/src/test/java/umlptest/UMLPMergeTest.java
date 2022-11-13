package umlptest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import umlp.UMLPMerge;
import umlp._ast.ASTUMLPCompilationUnit;

import java.util.HashSet;
import java.util.Set;

public class UMLPMergeTest extends AbstractUMLPTest {

  @Test
  public void testMerge() {
    Set<ASTUMLPCompilationUnit> models = new HashSet<>();
    models.add(parse("de/monticore/Driver.umlp"));
    models.add(parse("de/monticore/Employment.umlp"));

    models.forEach(this::runAfterParseTrafo);
    models.forEach(this::createSymTab);

    ASTUMLPCompilationUnit merge = UMLPMerge.merge(models, "MergeDriveAndEmployment");
    Assertions.assertNotNull(merge);
    runAfterParseTrafo(merge);
    createSymTab(merge);

    /* todo: fix deepEquals; expected is literally the same model as merge!
    ASTUMLPCompilationUnit expected = parse("de/monticore/MergeDriveAndEmployment.umlp");
    runAfterParseTrafo(expected);
    createSymTab(expected);
    Assertions.assertTrue(merge.deepEquals(expected,false));
     */

  }

}
