package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Collection;

public class Expression2BuilderPrinter extends Expression2SetterPrinter {

  protected SharedBoolean lastFieldAccess;

  public Expression2BuilderPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  public Expression2BuilderPrinter(
      IndentPrinter printer, boolean printComments, Collection<ASTExpression> ignore
  ) {
    super(printer, printComments, ignore);
  }

  @Override
  protected void resetPrinter() {
    super.resetPrinter();

    // customized printer
    CommonExpressionsPrettyPrinter commonExpressions = new FieldAccess2BuilderConverter(
        getPrinter(), printComments, allIgnore, lastFieldAccess
    );
    getTraverser().setCommonExpressionsHandler(commonExpressions);
    getTraverser().getCommonExpressionsVisitorList().clear();
    getTraverser().add4CommonExpressions(commonExpressions);

    // customized printer
    ExpressionsBasisPrettyPrinter expressionsBasis = new Name2BuilderConverter(
        getPrinter(), printComments, allIgnore, lastFieldAccess
    );
    getTraverser().setExpressionsBasisHandler(expressionsBasis);
    getTraverser().getExpressionsBasisVisitorList().clear();
    getTraverser().add4ExpressionsBasis(expressionsBasis);
  }

}
