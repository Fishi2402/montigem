/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos.cocos_for_students;

import de.monticore.cdbasis._ast.ASTCDInterfaceUsage;
import de.monticore.cdbasis._cocos.CDBasisASTCDInterfaceUsageCoCo;
import de.se_rwth.commons.logging.Log;

public class CDClassDoesNotImplementInterfaces implements CDBasisASTCDInterfaceUsageCoCo {

  @Override
  public void check(ASTCDInterfaceUsage node) {
    Log.error(String.format("0x78925: %s Class must not implement interfaces.", node.get_SourcePositionStart()));
  }
}
