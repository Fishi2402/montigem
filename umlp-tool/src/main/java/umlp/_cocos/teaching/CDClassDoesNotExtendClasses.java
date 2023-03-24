/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos.teaching;

import de.monticore.cdbasis._ast.ASTCDExtendUsage;
import de.monticore.cdbasis._cocos.CDBasisASTCDExtendUsageCoCo;
import de.se_rwth.commons.logging.Log;

public class CDClassDoesNotExtendClasses implements CDBasisASTCDExtendUsageCoCo {

  @Override
  public void check(ASTCDExtendUsage node) {
    Log.error(String.format("0x21309: %s Class must not inherit other classes", node.get_SourcePositionStart()));
  }
}
