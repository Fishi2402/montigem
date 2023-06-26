/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.guidsl._symboltable.IGUIDSLScope;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.Optional;

// TODO: remove when typecheck extended for GUI DSL
public class GUIExpressionTypeVisitor implements ExpressionsBasisVisitor2, CommonExpressionsVisitor2 {

  protected GUIDSLTraverser traverser;
  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  protected Optional<SymTypeExpression> res = Optional.empty();

  public GUIExpressionTypeVisitor() {
    traverser = GUIDSLMill.traverser();
    traverser.add4ExpressionsBasis(this);
    traverser.add4CommonExpressions(this);
  }

  public Optional<SymTypeExpression> getType(ASTExpression exp) {
    exp.accept(traverser);
    return res;
  }

  @Override
  public void endVisit(ASTNameExpression node) {
    Optional<VariableSymbol> vs = getScope(node.getEnclosingScope()).resolveVariable(node.getName());
    if (vs.isPresent()) {
      res = Optional.ofNullable(vs.get().getType());
    }
    else {
      Optional<GUIParamSymbol> ps = getScope(node.getEnclosingScope()).resolveGUIParam(node.getName());
      if (ps.isPresent()) {
        res = Optional.ofNullable(ps.get().getType());
      }
    }
  }

  @Override
  public void endVisit(ASTFieldAccessExpression node) {
    if (res.isPresent() && res.get().hasTypeInfo()) {
      for (VariableSymbol vs : res.get().getTypeInfo().getVariableList()) {
        if (vs.getName().equals(node.getName())) {
          res = Optional.ofNullable(vs.getType());
          break;
        }
      }
    }
  }

  protected IGUIDSLScope getScope(IScope scope) {
    if (scope instanceof IGUIDSLScope) {
      return (IGUIDSLScope) scope;
    }
    Log.error("0x00009 Detected rogue scope in GUI model");
    return null;
  }

}
