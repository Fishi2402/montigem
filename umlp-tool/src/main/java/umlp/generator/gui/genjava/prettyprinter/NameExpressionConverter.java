package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.function.Function;

public class NameExpressionConverter extends ExpressionsBasisPrettyPrinter {

  protected final Function<ASTNameExpression, String> conversion;

  public NameExpressionConverter(
      IndentPrinter printer,
      boolean printComments,
      Function<ASTNameExpression, String> conversion
  ) {
    super(printer, printComments);
    this.conversion = conversion;
  }

  @Override
  public void handle(ASTNameExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().print(conversion.apply(node) + " ");

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }


}
