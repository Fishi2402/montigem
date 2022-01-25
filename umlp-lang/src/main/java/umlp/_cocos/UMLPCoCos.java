/* (c) https://github.com/MontiCore/monticore */

package umlp._cocos;

import umlp._ast.ASTUMLPNode;

/**
 * Collection of all CoCos of the UMLP language
 */
public class UMLPCoCos {

  public static void checkAll(ASTUMLPNode node) {
    UMLPCoCoChecker umlpCoCoChecker = new UMLPCoCoChecker();
//    umlpCoCoChecker.addCoCo(new UseBlockOrder());
//    umlpCoCoChecker.addCoCo(new PrefixOnlyInFileBlock());
    umlpCoCoChecker.checkAll(node);
  }

}
