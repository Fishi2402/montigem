package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Collection;

public abstract class Expression2SetterPrinter extends ExpressionPrinter {

  public static final String SETTER_PARAM = "val_$";
  protected SharedBoolean lastFieldAccess;

  public Expression2SetterPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  public Expression2SetterPrinter(
      IndentPrinter printer, boolean printComments,
      Collection<ASTExpression> ignore, Collection<ASTExpression> nonClassMembers
  ) {
    super(printer, printComments, ignore, nonClassMembers);
  }

  @Override
  protected void resetPrinter() {
    super.resetPrinter();
    lastFieldAccess = new SharedBoolean(true);
  }

}
