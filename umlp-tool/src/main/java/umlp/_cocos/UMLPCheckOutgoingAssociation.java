/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssocSideCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.decorator.GenService;

public class UMLPCheckOutgoingAssociation implements CDAssociationASTCDAssocSideCoCo {

  public static final String ERROR_CODE = "0x70875";

  public static final String ERROR_MSG_FORMAT = " The class %s must not be the source of an association.";

  @Override
  public void check(ASTCDAssocSide assocSide) {
    SymTypeExpression sourceType = assocSide.getSymbol().getOtherSide().getType();
    if (assocSide.getSymbol().isIsDefinitiveNavigable() && (GenService.isJavaType(sourceType) || GenService.isCDEnum(sourceType))) {
      Log.error(String.format(ERROR_CODE+ ERROR_MSG_FORMAT, sourceType.print()), assocSide.get_SourcePositionStart());
    }
  }

}
