/* (c) https://github.com/MontiCore/monticore */

package umlp._cocos.teaching;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import umlp._cocos.UMLPCoCoChecker;

/**
 * Collection of teaching CoCos of the UMLP language
 */
public class UMLPTeachingCoCos {

  protected UMLPCoCoChecker checker = new UMLPCoCoChecker();

  public UMLPTeachingCoCos() {
    addCoCos(checker);
  }

  protected void addCoCos(UMLPCoCoChecker checker) {
    checker.addCoCo(new CDAssociationIsNotBidirectional());
    checker.addCoCo(new CDAssociationsAreNotCyclicAtLeastOne());
  }

  public void check(ASTCDCompilationUnit ast) {
    checker.checkAll(ast);
  }

}
