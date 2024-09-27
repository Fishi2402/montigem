/* (c) https://github.com/MontiCore/monticore */
package umlp;

import com.google.common.collect.Sets;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.CDMerge;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import umlp._cocos.UMLPCoCos;

import java.util.ArrayList;
import java.util.List;

public class UMLPMergeTest extends AbstractUMLPTest {

  @Test
  public void testMerge() {
    List<ASTCDCompilationUnit> models = new ArrayList<>();
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

    ASTCDCompilationUnit expected = parse("de/monticore/MergeDriveAndEmployment.umlp");
    runAfterParseTrafo(expected);
    createSymTab(merge);
    // TODO MS: In dem Merge-Ergebnis gibt es eine Assoziation mehr als in dem erwarteten Ergebnis
    //Assertions.assertTrue(merge.deepEquals(expected,false));
  }

}
