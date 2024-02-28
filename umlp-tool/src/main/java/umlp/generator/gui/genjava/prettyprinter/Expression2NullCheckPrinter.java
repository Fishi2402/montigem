package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Collection;
import java.util.HashSet;

public class Expression2NullCheckPrinter implements ExpressionsBasisVisitor2, CommonExpressionsVisitor2 {

  protected IndentPrinter printer;
  protected boolean printComments;
  protected GUIDSLTraverser traverser;
  protected final Collection<ASTExpression> ignore;

  protected StringBuilder sb;

  public Expression2NullCheckPrinter(IndentPrinter printer, boolean printComments) {
    this(printer, printComments, new HashSet<>());
  }

  public Expression2NullCheckPrinter(IndentPrinter printer, boolean printComments, Collection<ASTExpression> ignore) {
    traverser = GUIDSLMill.traverser();
    traverser.add4ExpressionsBasis(this);
    traverser.add4CommonExpressions(this);
    this.printer = printer;
    this.printComments = printComments;
    this.ignore = ignore;
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void endVisit(ASTFieldAccessExpression node) {
    Expression2AccessorPrinter e2a = new Expression2AccessorPrinter(printer, printComments, ignore);
    String check = e2a.prettyprint(node.getExpression()) + " == null";
    sb.append(" || " + check);
  }

  public String prettyprint(ASTNode node) {
    sb = new StringBuilder("false");
    printer.clearBuffer();
    getTraverser().clearTraversedElements();
    node.accept(getTraverser());
    printer.stripTrailing();
    return sb.toString();
  }

}
