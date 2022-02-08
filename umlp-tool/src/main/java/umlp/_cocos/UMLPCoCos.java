/* (c) https://github.com/MontiCore/monticore */

package umlp._cocos;

import de.monticore.cd4analysis.cocos.CD4AnalysisCoCos;
import umlp._ast.ASTUMLPNode;

/**
 * Collection of all CoCos of the UMLP language
 */
public class UMLPCoCos {

  public static void checkAll(ASTUMLPNode ast) {
    checkAll(ast, new UMLPCoCoChecker());
  }

  public static void checkAll(ASTUMLPNode ast, UMLPCoCoChecker checker) {
    // TODO Add custom cocos
    checker.checkAll(ast);

    checkAllCDCoCos(ast, checker);
    //TODO add for other languages too
  }

  public static void checkAllCDCoCos(ASTUMLPNode ast) {
    checkAllCDCoCos(ast, new UMLPCoCoChecker());
  }

  public static void checkAllCDCoCos(ASTUMLPNode ast, UMLPCoCoChecker checker) {
//    CD4AnalysisCoCos TODO
    CD4AnalysisCoCos c = new CD4AnalysisCoCos();
    checker.addChecker(c.getCheckerForAllCoCos());
  }

}
