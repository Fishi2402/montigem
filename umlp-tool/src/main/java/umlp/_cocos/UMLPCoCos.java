/* (c) https://github.com/MontiCore/monticore */

package umlp._cocos;

import de.monticore.cd4analysis.cocos.CD4AnalysisCoCos;
import de.monticore.cd4analysis.cocos.CD4AnalysisCoCosDelegator;
import umlp._ast.ASTUMLPNode;

/**
 * Collection of all CoCos of the UMLP language
 */
public class UMLPCoCos {

  public static void checkAll(ASTUMLPNode ast) {
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
  }
}
