/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import de.monticore.cd4code.typescalculator.FullSynthesizeFromCD4Code;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;


public class UMLPAssociationQualifierByAttributeName implements CDAssociationASTCDAssociationCoCo {

  public static final String TYPE_ERROR_CODE = "0xCDC68";
  public static final String NO_ATTR_ERROR_CODE = "0xCDC69";

  public static final String TYPE_ERROR_MSG_FORMAT = " Qualified association with attribute %s " +
      "has incompatible type, qualified associations only support numeric or string type qualifiers.";
  public static final String NO_ATTR_ERROR_MSG_FORMAT = " Attribute %s of qualified association " +
      "does not exist in class %s.";
  private FullSynthesizeFromCD4Code synthesizer;
  private WithinTypeBasicSymbolsResolver resolver;
  public UMLPAssociationQualifierByAttributeName(){
    this.synthesizer = new FullSynthesizeFromCD4Code();
    this.resolver = new WithinTypeBasicSymbolsResolver();
  }
  @Override
  public void check(ASTCDAssociation assoc) {
    if (assoc.getLeft().isPresentCDQualifier() && assoc.getLeft().getCDQualifier()
        .isPresentByAttributeName()) {
      checkTypeExists(assoc.getLeft(), assoc.getRight());

    }else if (assoc.getRight().isPresentCDQualifier() && assoc.getRight().getCDQualifier()
        .isPresentByAttributeName()) {
      checkTypeExists(assoc.getRight(), assoc.getLeft());
    }
  }

  private void checkTypeExists(ASTCDAssocSide qSide, ASTCDAssocSide attrSide) {
    SymTypeExpression ste = this.synthesizer.synthesizeType(attrSide.getMCQualifiedType()).getResult();
    final String attributeName = qSide.getCDQualifier().getByAttributeName();
    Optional<SymTypeExpression> expr = this.resolver.resolveVariable(ste, attributeName,
        BasicAccessModifier.ALL_INCLUSION, x -> true);
    if(expr.isPresent()){
      if (!SymTypeRelations.isNumericType(expr.get()) && !SymTypeRelations.isString(expr.get())){
        Log.error(
            String.format(TYPE_ERROR_CODE + TYPE_ERROR_MSG_FORMAT,
               attributeName),
            qSide.getCDQualifier().get_SourcePositionStart());
      }
    }else{
      Log.error(
          String.format(
              NO_ATTR_ERROR_CODE + NO_ATTR_ERROR_MSG_FORMAT,
              attributeName, attrSide.getMCQualifiedType().getMCQualifiedName()), qSide.getCDQualifier().get_SourcePositionStart());
    }
  }
}
