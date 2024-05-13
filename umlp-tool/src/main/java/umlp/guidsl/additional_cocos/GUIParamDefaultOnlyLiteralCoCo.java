package umlp.guidsl.additional_cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._cocos.GUIDSLASTGUIParamCoCo;
import de.se_rwth.commons.logging.Log;

public class GUIParamDefaultOnlyLiteralCoCo implements GUIDSLASTGUIParamCoCo{

  @Override
  public void check(ASTGUIParam node) {
    if(node.isPresentDefault() && !(node.getDefault() instanceof ASTLiteralExpression)){
      Log.error("Default value of parameter "
              + GUIDSLMill.prettyPrint(node, false) + " is not a literal, which is currently the only supported kind.",
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
