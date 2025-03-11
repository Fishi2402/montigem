package umlp.backendrte.ocl;

import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.literals.mccommonliterals._prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.ocl.ocl._prettyprint.OCLFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class CommonExpressions2SQLFullPrinter extends OCLFullPrettyPrinter {

  public CommonExpressions2SQLFullPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  protected void initializeTraverser(boolean printComments) {
    super.initializeTraverser(printComments);

    // replace pretty printer via custom one
    CommonExpressionsPrettyPrinter commonExpressions = new CommonExpressions2SQLPrinter(getPrinter(), printComments);
    getTraverser().setCommonExpressionsHandler(commonExpressions);
    getTraverser().getCommonExpressionsVisitorList().clear();
    getTraverser().add4CommonExpressions(commonExpressions);
  }

}
