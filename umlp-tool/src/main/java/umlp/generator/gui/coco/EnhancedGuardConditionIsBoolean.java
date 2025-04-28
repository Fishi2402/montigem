package umlp.generator.gui.coco;

import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._cocos.GUIDSLASTGUIGuardCoCo;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class EnhancedGuardConditionIsBoolean implements GUIDSLASTGUIGuardCoCo {
  @Override
  public void check(ASTGUIGuard node) {
    if (!GUIDSLMill.typeDispatcher().isCommonExpressionsASTCallExpression(node.getCondition())
        && !SymTypeRelations.isBoolean(TypeCheck3.typeOf(node.getCondition()))) {
      Log.error("0x00009 The condition of a guard must be evaluated to the type boolean",
          node.get_SourcePositionStart());
    }
  }
}
