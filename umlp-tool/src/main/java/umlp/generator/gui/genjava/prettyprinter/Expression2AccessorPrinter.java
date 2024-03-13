package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Collection;

public class Expression2AccessorPrinter extends ExpressionPrinter {

  public Expression2AccessorPrinter(
      IndentPrinter printer, boolean printComments
  ) {
    super(printer, printComments);
  }

  public Expression2AccessorPrinter(
      IndentPrinter printer, boolean printComments,
      Collection<ASTExpression> ignore, Collection<ASTExpression> nonClassMembers
  ) {
    super(printer, printComments, ignore, nonClassMembers);
  }

  @Override
  protected void resetPrinter() {
    // customized printer
    super.resetPrinter();
    CommonExpressionsPrettyPrinter commonExpressions = new FieldAccess2AccessorConverter(
        getPrinter(), printComments, allIgnore
    );
    getTraverser().setCommonExpressionsHandler(commonExpressions);
    getTraverser().getCommonExpressionsVisitorList().clear();
    getTraverser().add4CommonExpressions(commonExpressions);

    // customized printer
    ExpressionsBasisPrettyPrinter expressionsBasis = new Name2AccessorConverter(
        getPrinter(), printComments, allIgnore, nonClassMembers
    );
    getTraverser().setExpressionsBasisHandler(expressionsBasis);
    getTraverser().getExpressionsBasisVisitorList().clear();
    getTraverser().add4ExpressionsBasis(expressionsBasis);
  }

}
