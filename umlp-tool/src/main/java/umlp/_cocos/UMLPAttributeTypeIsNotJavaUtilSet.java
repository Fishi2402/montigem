package umlp._cocos;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import umlp.generator.domain.decorator.GenService;

public class UMLPAttributeTypeIsNotJavaUtilSet implements CDBasisASTCDAttributeCoCo, CoCoTestable {

  public static final String ERROR_CODE = "0x70884";
  public static final String ERROR_MSG_FORMAT = " java.util.Set is not yet supported as attribute type. Use java.util.List instead.";

  @Override
  public void check(ASTCDAttribute attr) {
    if (GenService.isSet(attr)) {
      logError(attr);
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
