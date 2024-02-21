package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class Expression2BuilderPrinter extends Expression2AccessorPrinter {

  protected SharedBoolean lastFieldAccess;

  public Expression2BuilderPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  protected void resetPrinter() {
    super.resetPrinter();
    // customized printer
    lastFieldAccess = new SharedBoolean(true);
    CommonExpressionsPrettyPrinter commonExpressions = new FieldAccess2BuilderConverter(
        getPrinter(), printComments, ignore, lastFieldAccess
    );
    getTraverser().setCommonExpressionsHandler(commonExpressions);
    getTraverser().getCommonExpressionsVisitorList().clear();
    getTraverser().add4CommonExpressions(commonExpressions);

    // customized printer
    ExpressionsBasisPrettyPrinter expressionsBasis = new Name2BuilderConverter(
        getPrinter(), printComments, ignore, lastFieldAccess
    );
    getTraverser().setExpressionsBasisHandler(expressionsBasis);
    getTraverser().getExpressionsBasisVisitorList().clear();
    getTraverser().add4ExpressionsBasis(expressionsBasis);
  }

}
