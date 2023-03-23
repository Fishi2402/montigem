/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos.cocos_for_students;

import de.monticore.cdassociation._ast.ASTCDBiDir;
import de.monticore.cdassociation._cocos.CDAssociationASTCDBiDirCoCo;
import de.se_rwth.commons.logging.Log;

public class CDAssociationIsNotBidirectional implements CDAssociationASTCDBiDirCoCo {

  @Override
  public void check(ASTCDBiDir node) {
    Log.error(String.format("0x54277: %s Bidirectional Associations must not exist in Diagramm", node.get_SourcePositionStart()));
  }
}
