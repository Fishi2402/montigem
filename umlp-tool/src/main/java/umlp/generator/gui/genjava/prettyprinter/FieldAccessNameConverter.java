package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.function.Function;

public class FieldAccessNameConverter extends CommonExpressionsPrettyPrinter {

  protected final Function<ASTFieldAccessExpression, String> conversion;

  public FieldAccessNameConverter(
      IndentPrinter printer,
      boolean printComments,
      Function<ASTFieldAccessExpression, String> conversion
  ) {
    super(printer, printComments);
    this.conversion = conversion;
  }

  @Override
  public void handle(ASTFieldAccessExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getExpression().accept(getTraverser());

    getPrinter().stripTrailing();
    getPrinter().print(".");

    getPrinter().print(conversion.apply(node) + " ");

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }

  }


}
