/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.se_rwth.commons.logging.Log;

public class UMLPCheckAttributeModifier implements CDBasisASTCDAttributeCoCo {

  public static final String ERROR_CODE = "0x70874";

  public static final String ERROR_MSG_FORMAT = " The private modifier of attribute '%s' is currently not supported. Please change your modifier";

  @Override
  public void check(ASTCDAttribute attr) {
    if (attr.getModifier().isPrivate()) {
      Log.error(String.format(ERROR_CODE+ ERROR_MSG_FORMAT, attr.getName(), attr.get_SourcePositionStart()));
    }
  }

}
