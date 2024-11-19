/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;


public class UMLPAssociationCardinality implements CDAssociationASTCDAssociationCoCo {

  public static final String ERROR_CODE = "0x70879";
  public static final String ERROR_MSG_FORMAT = " Cardinality %s is invalid, custom cardinalities aren't supported. " +
      "associations only support default, 1, 0..1 and * cardinalties.";

  @Override
  public void check(ASTCDAssociation a) {
    if (a.getLeft().isPresentCDCardinality()) {
      checkCardinality(a.getLeft().getCDCardinality());
    }
    if (a.getRight().isPresentCDCardinality()) {
      checkCardinality(a.getRight().getCDCardinality());
    }
  }

  private void checkCardinality(ASTCDCardinality card) {
    if (!card.isMult() && (card.getLowerBound() > 1 || card.getUpperBound() > 1)) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, card), card.get_SourcePositionStart());
    }
  }
}
