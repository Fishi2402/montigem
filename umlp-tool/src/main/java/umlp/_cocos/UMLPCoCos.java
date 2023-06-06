/* (c) https://github.com/MontiCore/monticore */

package umlp._cocos;

import de.monticore.cd4analysis.cocos.CD4AnalysisCoCosDelegator;
import de.monticore.cdassociation.cocos.ebnf.RoleAndFieldNamesUnique;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

/**
 * Collection of all CoCos of the UMLP language
 */
public class UMLPCoCos {

  public static void checkAll(ASTCDCompilationUnit ast) {
    UMLPCoCoChecker checker = new UMLPCoCoChecker();
    addAllUMLPCoCos(checker);
    addAllCDCoCos(checker);
    checker.checkAll(ast);
  }

  public static void addAllUMLPCoCos(UMLPCoCoChecker checker){
    // TODO add cocos, once these are implemented
  }

  public static void addAllCDCoCos(UMLPCoCoChecker checker){
    checker.addChecker(new CD4AnalysisCoCosDelegator().getCheckerForAllCoCos());
    // CoCos for generation
    checker.addCoCo(new RoleAndFieldNamesUnique());
    checker.addCoCo(new UMLPCheckDiagramName());
  }
}
