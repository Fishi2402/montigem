package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.List;
import java.util.function.Function;

public class FieldAccessNameConverter extends CommonExpressionsPrettyPrinter {

  protected final Function<ASTFieldAccessExpression, String> conversion;
  protected final List<ASTExpression> ignore;

  public FieldAccessNameConverter(
      IndentPrinter printer,
      boolean printComments,
      Function<ASTFieldAccessExpression, String> conversion,
      List<ASTExpression> ignore
  ) {
    super(printer, printComments);
    this.conversion = conversion;
    this.ignore = ignore;
  }

  public void handle(ASTCallExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    // ignore field and name expressions that are direct children of ASTCallExpression
    ignore.add(node.getExpression());

    node.getExpression().accept(getTraverser());
    node.getArguments().accept(getTraverser());
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTFieldAccessExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getExpression().accept(getTraverser());

    getPrinter().stripTrailing();
    getPrinter().print(".");

    if (ignore.contains(node)) {
      getPrinter().print(node.getName() + " ");
    }
    else {
      getPrinter().print(conversion.apply(node) + " ");
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }

  }


}
