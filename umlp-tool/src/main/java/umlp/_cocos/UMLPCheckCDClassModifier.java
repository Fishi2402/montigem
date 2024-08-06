/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

public class UMLPCheckCDClassModifier implements CDBasisASTCDClassCoCo {

  public static final String ERROR_CODE = "0x70876";

  public static final String ERROR_MSG_FORMAT = " The derived modifier of class '%s' is currently not supported. Please change your modifier";

  @Override
  public void check(ASTCDClass clazz) {
    if (clazz.getModifier().isDerived()) {
      Log.error(String.format(ERROR_CODE+ ERROR_MSG_FORMAT, clazz.getName()), clazz.get_SourcePositionStart());
    }
  }

}
