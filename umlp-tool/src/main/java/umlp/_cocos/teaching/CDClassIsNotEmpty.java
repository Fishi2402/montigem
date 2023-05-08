/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos.teaching;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

public class CDClassIsNotEmpty implements CDBasisASTCDClassCoCo {

  @Override
  public void check(ASTCDClass node) {
    if (node.getCDMemberList().isEmpty()) {
      Log.error(String.format("0x70896: %s Class must not be empty.", node.get_SourcePositionStart()));
    }
  }
}
