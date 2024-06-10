package umlp.guidsl.additional_cocos;

import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._cocos.GUIDSLASTGUICompilationUnitCoCo;

public class NameExpressionsSymbolBoundExceptCallsCoCo implements GUIDSLASTGUICompilationUnitCoCo {

  @Override
  public void check(ASTGUICompilationUnit node) {
    node.accept(SymbolBoundNameExpressionsFinder.getInstance().getTraverser());
  }

}
