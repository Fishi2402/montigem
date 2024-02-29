package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Collection;

public class Expression2MutatorPrinter extends Expression2SetterPrinter {

  public Expression2MutatorPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  public Expression2MutatorPrinter(
      IndentPrinter printer, boolean printComments,
      Collection<ASTExpression> ignore, Collection<ASTExpression> nonClassMembers
  ) {
    super(printer, printComments, ignore, nonClassMembers);
  }

  @Override
  protected void resetPrinter() {
    super.resetPrinter();
    // customized printer
    CommonExpressionsPrettyPrinter commonExpressions = new FieldAccess2MutatorConverter(
        getPrinter(), printComments, allIgnore, lastFieldAccess
    );
    getTraverser().setCommonExpressionsHandler(commonExpressions);
    getTraverser().getCommonExpressionsVisitorList().clear();
    getTraverser().add4CommonExpressions(commonExpressions);

    // customized printer
    ExpressionsBasisPrettyPrinter expressionsBasis = new Name2MutatorConverter(
        getPrinter(), printComments, allIgnore, nonClassMembers, lastFieldAccess
    );
    getTraverser().setExpressionsBasisHandler(expressionsBasis);
    getTraverser().getExpressionsBasisVisitorList().clear();
    getTraverser().add4ExpressionsBasis(expressionsBasis);
  }

}
