package umlp.generator.gui.genjava.decorator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._visitor.GUIDSLInheritanceHandler;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IgnoreExpressionsFinder extends GUIDSLInheritanceHandler implements ExpressionsBasisVisitor2 {

  protected List<ASTExpression> ignore = new ArrayList<>();
  protected final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  public IgnoreExpressionsFinder() {
    getTraverser().setGUIDSLHandler(this);
    getTraverser().add4ExpressionsBasis(this);
  }

  public List<ASTExpression> getIgnore(ASTGUICompilationUnit ast) {
    ast.accept(getTraverser());
    return ignore;
  }

  @Override
  public void visit(ASTExpression node) {
    Optional<VariableSymbol> vs = util.findIterationItemSymbols(
        util.print(node), node.getEnclosingScope()
    );
    if (vs.isPresent()) {
      ignore.add(node);
    }
  }

}
