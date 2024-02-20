package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Expression2AccessorPrinter extends GUIDSLFullPrettyPrinter {

  protected final boolean printComments;

  public Expression2AccessorPrinter(
      IndentPrinter printer,
      boolean printComments
  ) {
    super(printer, printComments);
    this.printComments = printComments;
  }

  @Override
  protected void initializeTraverser(boolean printComments) {
    super.initializeTraverser(printComments);

    Set<ASTExpression> ignore = new HashSet<>();
    // customized printer
    CommonExpressionsPrettyPrinter commonExpressions = new FieldAccess2AccessorConverter(
        getPrinter(), printComments, ignore
    );
    getTraverser().setCommonExpressionsHandler(commonExpressions);
    getTraverser().getCommonExpressionsVisitorList().clear();
    getTraverser().add4CommonExpressions(commonExpressions);

    // customized printer
    ExpressionsBasisPrettyPrinter expressionsBasis = new Name2AccessorConverter(
        getPrinter(), printComments, ignore
    );
    getTraverser().setExpressionsBasisHandler(expressionsBasis);
    getTraverser().getExpressionsBasisVisitorList().clear();
    getTraverser().add4ExpressionsBasis(expressionsBasis);
  }

}
