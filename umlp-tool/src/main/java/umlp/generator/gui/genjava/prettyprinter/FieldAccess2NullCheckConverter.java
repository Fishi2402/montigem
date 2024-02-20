package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.List;
import java.util.function.Function;

public class FieldAccess2NullCheckConverter extends CommonExpressionsPrettyPrinter {

  protected final List<ASTExpression> ignore;
  protected boolean skipNext = true;
  public FieldAccess2NullCheckConverter(
      IndentPrinter printer,
      boolean printComments,
      Function<ASTFieldAccessExpression, String> conversion,
      List<ASTExpression> ignore
  ) {
    super(printer, printComments);
    this.ignore = ignore;
  }

  public void handle(ASTCallExpression node) {
    // ignore field and name expressions that are direct children of ASTCallExpression
    ignore.add(node.getExpression());
    super.handle(node);
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
      // getPrinter().print(conversion.apply(node) + " ");
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }

  }


}
