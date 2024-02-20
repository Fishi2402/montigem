package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.ArrayList;
import java.util.List;

public class CommonExpressionsConversionPrinter extends GUIDSLFullPrettyPrinter {

  protected final boolean printComments;

  public CommonExpressionsConversionPrinter(
      IndentPrinter printer,
      boolean printComments
  ) {
    super(printer, printComments);
    this.printComments = printComments;
  }

  public void asAccessorConverter() {
    List<ASTExpression> ignore = new ArrayList<>();
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
