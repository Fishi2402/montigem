/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos.cocos_for_students;

import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

public class CDInterfaceNotPresent implements CDInterfaceAndEnumASTCDInterfaceCoCo {

  @Override
  public void check(ASTCDInterface node) {
    Log.error(String.format("0x24378: %s Interface may not exist in Diagram", node.get_SourcePositionStart()));
  }
}
