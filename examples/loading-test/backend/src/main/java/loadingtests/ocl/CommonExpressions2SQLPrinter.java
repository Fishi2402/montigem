package loadingtests.ocl;

import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class CommonExpressions2SQLPrinter extends CommonExpressionsPrettyPrinter {

  public CommonExpressions2SQLPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTEqualsExpression node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getLeft().accept(getTraverser());
    getPrinter().stripTrailing();

    // adjusted part
    getPrinter().print("=");

    node.getRight().accept(getTraverser());
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
