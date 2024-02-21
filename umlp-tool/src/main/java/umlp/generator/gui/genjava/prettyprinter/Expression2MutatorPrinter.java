package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class Expression2MutatorPrinter extends Expression2AccessorPrinter {

  public static final String MUTATOR_PARAM = "val";
  protected SharedBoolean lastFieldAccess;

  public Expression2MutatorPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  protected void resetPrinter() {
    super.resetPrinter();
    // customized printer
    lastFieldAccess = new SharedBoolean(true);
    CommonExpressionsPrettyPrinter commonExpressions = new FieldAccess2MutatorConverter(
        getPrinter(), printComments, ignore, lastFieldAccess
    );
    getTraverser().setCommonExpressionsHandler(commonExpressions);
    getTraverser().getCommonExpressionsVisitorList().clear();
    getTraverser().add4CommonExpressions(commonExpressions);

    // customized printer
    ExpressionsBasisPrettyPrinter expressionsBasis = new Name2MutatorConverter(
        getPrinter(), printComments, ignore, lastFieldAccess
    );
    getTraverser().setExpressionsBasisHandler(expressionsBasis);
    getTraverser().getExpressionsBasisVisitorList().clear();
    getTraverser().add4ExpressionsBasis(expressionsBasis);
  }

}
