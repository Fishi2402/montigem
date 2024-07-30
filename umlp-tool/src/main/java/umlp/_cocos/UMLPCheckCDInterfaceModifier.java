/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

public class UMLPCheckCDInterfaceModifier implements CDInterfaceAndEnumASTCDInterfaceCoCo {

  public static final String ERROR_CODE = "0x70878";

  public static final String ERROR_MSG_FORMAT = " The derived modifier of interface '%s' is currently not supported. Please change your modifier";

  @Override
  public void check(ASTCDInterface interf) {
    if (interf.getModifier().isDerived()) {
      Log.error(String.format(ERROR_CODE+ ERROR_MSG_FORMAT, interf.getName()), interf.get_SourcePositionStart());
    }
  }

}
