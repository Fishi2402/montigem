package umlp.generator.gui.coco;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._symboltable.IGUIDSLScope;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.Optional;

public class SymbolBoundNameExpressionsFinder implements GUIDSLVisitor2, CommonExpressionsVisitor2, ExpressionsBasisVisitor2 {

  protected GUIDSLTraverser traverser;

  protected static SymbolBoundNameExpressionsFinder instance;

  protected Optional<ASTCallExpression> holder = Optional.empty();

  public static SymbolBoundNameExpressionsFinder getInstance() {
    if (instance == null) {
      instance = new SymbolBoundNameExpressionsFinder();
    }
    return instance;
  }

  public static void unsetInstance() {
    instance = null;
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  private SymbolBoundNameExpressionsFinder() {
    traverser = GUIDSLMill.traverser();
    traverser.add4CommonExpressions(this);
    traverser.add4ExpressionsBasis(this);
  }

  @Override
  public void visit(ASTNameExpression node) {
    if (!(holder.isPresent() && holder.get().getExpression().equals(node))) {
      Optional<IGUIDSLScope> scope = GUIGeneratorUtil.getInstance().findClosestGUIDSLScope(node.getEnclosingScope());
      if (scope.isPresent()) {
        Optional<TypeSymbol> ts = scope.get().resolveType(node.getName());
        Optional<VariableSymbol> vs = scope.get().resolveVariable(node.getName());
        if (ts.isEmpty() && vs.isEmpty()) {
          Log.error("0x00022 Found an unknown variable or type '" + node.getName()
              + "' at " + node.get_SourcePositionStart().toString()
              + " - " + node.get_SourcePositionEnd().toString()
              + ". If you intended to implement a hand-written extension please use a call instead,"
              + " e.g., " + node.getName() + "()"
          );
        }
      }
    }
  }

  @Override
  public void visit(ASTCallExpression node) {
    holder = Optional.of(node);
  }
}
