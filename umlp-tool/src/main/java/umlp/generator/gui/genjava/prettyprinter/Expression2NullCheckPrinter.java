package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import umlp.generator.gui.GUIGeneratorUtil;

public class Expression2NullCheckPrinter implements ExpressionsBasisVisitor2, CommonExpressionsVisitor2 {

  protected Expression2AccessorPrinter printer;
  protected GUIDSLTraverser traverser;

  protected StringBuilder sb;

  public Expression2NullCheckPrinter(
      Expression2AccessorPrinter printer
  ) {
    traverser = GUIDSLMill.traverser();
    traverser.add4ExpressionsBasis(this);
    traverser.add4CommonExpressions(this);
    this.printer = printer;
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void endVisit(ASTFieldAccessExpression node) {
    if (!GUIGeneratorUtil.getInstance().isEnumMember(node)) {
      String check = printer.prettyprint(node.getExpression()) + " == null";
      sb.append(" || ").append(check);
    }
  }

  public String prettyprint(ASTNode node) {
    sb = new StringBuilder("false");
    getTraverser().clearTraversedElements();
    node.accept(getTraverser());
    return sb.toString();
  }

}
