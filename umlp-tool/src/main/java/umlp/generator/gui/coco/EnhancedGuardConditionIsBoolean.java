package umlp.generator.gui.coco;

import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._cocos.GUIDSLASTGUIGuardCoCo;
import de.monticore.guidsl._cocos.TypeCheckCoCo;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

public class EnhancedGuardConditionIsBoolean extends TypeCheckCoCo implements GUIDSLASTGUIGuardCoCo {
  @Override
  public void check(ASTGUIGuard node) {
    setup(node);
    if (!GUIDSLMill.typeDispatcher().isCommonExpressionsASTCallExpression(node.getCondition())
        && !SymTypeRelations.isBoolean(type4Ast.getTypeOfExpression(node.getCondition()))) {
      Log.error("0x00009 The condition of a guard must be evaluated to the type boolean",
          node.get_SourcePositionStart());
    }
  }
}
