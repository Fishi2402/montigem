/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDEnumCoCo;
import de.se_rwth.commons.logging.Log;

public class UMLPCheckCDEnumModifier implements CDInterfaceAndEnumASTCDEnumCoCo {

  public static final String ERROR_CODE = "0x70877";

  public static final String ERROR_MSG_FORMAT = " The derived modifier of enum '%s' is currently not supported. Please change your modifier";

  @Override
  public void check(ASTCDEnum astEnum) {
    if (astEnum.getModifier().isDerived()) {
      Log.error(String.format(ERROR_CODE+ ERROR_MSG_FORMAT, astEnum.getName()), astEnum.get_SourcePositionStart());
    }
  }

}
