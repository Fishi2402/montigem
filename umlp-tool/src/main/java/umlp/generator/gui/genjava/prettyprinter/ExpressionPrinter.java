package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.guidsl._prettyprint.GUIDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Collection;
import java.util.HashSet;

public abstract class ExpressionPrinter extends GUIDSLFullPrettyPrinter {

  protected Collection<ASTExpression> allIgnore;
  protected final Collection<ASTExpression> ignore;
  protected final Collection<ASTExpression> nonClassMembers;
  protected final boolean printComments;

  public ExpressionPrinter(IndentPrinter printer, boolean printComments) {
    this(printer, printComments, new HashSet<>(), new HashSet<>());
  }

  public ExpressionPrinter(
      IndentPrinter printer, boolean printComments,
      Collection<ASTExpression> ignore, Collection<ASTExpression> nonClassMembers
  ) {
    super(printer, printComments);
    this.printComments = printComments;
    this.ignore = ignore;
    this.nonClassMembers = nonClassMembers;
  }

  @Override
  protected void initializeTraverser(boolean printComments) {
    super.initializeTraverser(printComments);
    resetPrinter();
  }

  protected void resetPrinter() {
    allIgnore = ignore;

    GUIDSLPrettyPrinter gUIDSL = new GUIList2JavaConverter(getPrinter(), printComments);
    getTraverser().setGUIDSLHandler(gUIDSL);
    getTraverser().getGUIDSLVisitorList().clear();
    getTraverser().add4GUIDSL(gUIDSL);
  }

  @Override
  public String prettyprint(ASTNode node) {
    resetPrinter();
    return super.prettyprint(node);
  }
}
