/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos.teaching;

import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDEnumCoCo;
import de.se_rwth.commons.logging.Log;

public class CDEnumNotPresent implements CDInterfaceAndEnumASTCDEnumCoCo {

  @Override
  public void check(ASTCDEnum node) {
    Log.error(String.format("0x27089: %s Enumeration must not exist in Diagram", node.get_SourcePositionStart()));
  }
}
