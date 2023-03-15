/* (c) https://github.com/MontiCore/monticore */
package umlptest;

import com.google.common.collect.Sets;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.CDMerge;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import umlp._cocos.UMLPCoCos;

import java.util.HashSet;
import java.util.Set;

public class UMLPMergeTest extends AbstractUMLPTest {

  @Test
  public void testMerge() {
    Set<ASTCDCompilationUnit> models = new HashSet<>();
    models.add(parse("de/monticore/Driver.umlp"));
    models.add(parse("de/monticore/Employment.umlp"));

    models.forEach(this::runAfterParseTrafo);
    models.forEach(this::createSymTab);

    models.forEach(UMLPCoCos::checkAll);

    ASTCDCompilationUnit merge = CDMerge.merge(models, "MergeDriveAndEmployment", Sets.newHashSet());
    Assertions.assertNotNull(merge);
    runAfterParseTrafoForMerge(merge);
    createSymTab(merge);
    UMLPCoCos.checkAll(merge);

    /* todo: fix deepEquals; expected is literally the same model as merge!
    ASTUMLPCompilationUnit expected = parse("de/monticore/MergeDriveAndEmployment.umlp");
    runAfterParseTrafo(expected);
    createSymTab(expected);
    Assertions.assertTrue(merge.deepEquals(expected,false));
     */

  }

}
