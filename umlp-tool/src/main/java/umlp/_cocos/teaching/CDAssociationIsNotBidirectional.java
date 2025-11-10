/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos.teaching;

import de.monticore.cdassociation._ast.ASTCDBiDir;
import de.monticore.cdassociation._cocos.CDAssociationASTCDBiDirCoCo;
import umlp._cocos.CoCoTestable;

public class CDAssociationIsNotBidirectional implements CDAssociationASTCDBiDirCoCo, CoCoTestable {

  @Override
  public void check(ASTCDBiDir node) {
    logError(node);
  }

  @Override
  public String getErrorCode() {
    return "0x54277";
  }

  @Override
  public String getErrorMessageFormat() {
    return "Bidirectional Associations must not exist in Diagramm";
  }
}
