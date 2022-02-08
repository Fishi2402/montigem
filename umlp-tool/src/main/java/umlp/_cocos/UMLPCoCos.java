/* (c) https://github.com/MontiCore/monticore */

package umlp._cocos;

import de.monticore.cd4analysis.cocos.CD4AnalysisCoCos;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation.cocos.CDAssociationCoCos;
import de.monticore.cdinterfaceandenum.cocos.CDInterfaceAndEnumCoCos;
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
    CD4AnalysisCoCos analysisCoCos = new CD4AnalysisCoCos();
    checker.addChecker(analysisCoCos.getCheckerForAllCoCos());
  }

}
