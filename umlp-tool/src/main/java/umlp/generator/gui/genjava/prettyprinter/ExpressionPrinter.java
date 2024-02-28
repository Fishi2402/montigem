package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Collection;
import java.util.HashSet;

public abstract class ExpressionPrinter extends GUIDSLFullPrettyPrinter {

  protected Collection<ASTExpression> allIgnore;
  protected final Collection<ASTExpression> ignore;
  protected final boolean printComments;

  public ExpressionPrinter(IndentPrinter printer, boolean printComments) {
    this(printer, printComments, new HashSet<>());
  }

  public ExpressionPrinter(
      IndentPrinter printer, boolean printComments, Collection<ASTExpression> ignore
  ) {
    super(printer, printComments);
    this.printComments = printComments;
    this.ignore = ignore;
  }

  @Override
  protected void initializeTraverser(boolean printComments) {
    super.initializeTraverser(printComments);
    resetPrinter();
  }

  protected void resetPrinter() {
    allIgnore = ignore;
  }

  @Override
  public String prettyprint(ASTNode node) {
    resetPrinter();
    return super.prettyprint(node);
  }
}
