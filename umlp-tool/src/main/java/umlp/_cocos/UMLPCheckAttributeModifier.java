/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;

public class UMLPCheckAttributeModifier implements CDBasisASTCDAttributeCoCo, CoCoTestable {

  public static final String ERROR_CODE = "0x70874";

  public static final String ERROR_MSG_FORMAT = " The private modifier of attribute '%s' is currently not supported. Please change your modifier";

  @Override
  public void check(ASTCDAttribute attr) {
    if (attr.getModifier().isPrivate()) {
      logError(attr, attr.getName());
    }
  }

  @Override
  public String getErrorCode() {
    return ERROR_CODE;
  }

  @Override
  public String getErrorMessageFormat() {
    return ERROR_MSG_FORMAT;
  }
}
