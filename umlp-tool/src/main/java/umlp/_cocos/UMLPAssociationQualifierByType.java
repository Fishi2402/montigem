/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import de.monticore.cd4code.typescalculator.FullSynthesizeFromCD4Code;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;


public class UMLPAssociationQualifierByType implements CDAssociationASTCDAssociationCoCo {

  public static final String ERROR_CODE = "0xCDC63";
  public static final String ERROR_MSG_FORMAT = " Qualified association with type %s is invalid, " +
      "qualified associations only support numeric or string type qualifiers.";
  @Override
  public void check(ASTCDAssociation a) {
    if (a.getLeft().isPresentCDQualifier() && a.getLeft().getCDQualifier().isPresentByType()) {
      checkSimpleType(a.getLeft().getCDQualifier().getByType());
    } else if (a.getRight().isPresentCDQualifier() && a.getRight().getCDQualifier().isPresentByType()) {
      checkSimpleType(a.getRight().getCDQualifier().getByType());
    }
  }
  private void checkSimpleType(ASTMCType type) {
    FullSynthesizeFromCD4Code synthesizer = new FullSynthesizeFromCD4Code();
    SymTypeExpression ste = synthesizer.synthesizeType(type).getResult();
    if (!SymTypeRelations.isNumericType(ste) && !SymTypeRelations.isString(ste)){
      Log.error(
        String.format(
          ERROR_CODE + ERROR_MSG_FORMAT,
          ste.getTypeInfo().getName()),
        type.get_SourcePositionStart());
    }
  }
}
