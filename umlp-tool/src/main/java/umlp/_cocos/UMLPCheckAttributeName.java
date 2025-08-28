package umlp._cocos;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.se_rwth.commons.StringTransformations;

import java.util.List;

public class UMLPCheckAttributeName implements CDBasisASTCDAttributeCoCo, CoCoTestable {

  public static final String ERROR_CODE = "0x19134";
  public static final String ERROR_MSG_FORMAT = " Bad attribute name '%s'.";

  private static final List<String> BAD_ATTRIBUTE_NAMES = List.of("id", "commandType", "participantId", "effectedGemIds", "newlySubscribedGemIds");

  @Override
  public void check(ASTCDAttribute attr) {
    if (BAD_ATTRIBUTE_NAMES.contains(StringTransformations.uncapitalize(attr.getName()))) {
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
