/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

public class UMLPCheckDerivedAttribute implements CDBasisASTCDClassCoCo {

  public static final String ERROR_CODE = "0x70880";

  public static final String ERROR_MSG_FORMAT = " The derived attribute '%s' was not correctly overridden.";

  @Override
  public void check(ASTCDClass clazz) {
    clazz.getCDAttributeList().stream().filter(a -> !a.getModifier().isDerived()).forEach(attr -> {
      CDSymbolTables.getInheritedAttributesInHierarchy(clazz).forEach(a -> {
        if (a.getModifier().isDerived()) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, attr.getName()), attr.get_SourcePositionStart());
        }
      });
    });
  }

}
