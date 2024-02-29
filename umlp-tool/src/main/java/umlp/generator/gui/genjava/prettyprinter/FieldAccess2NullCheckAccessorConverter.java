package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Collection;

public class FieldAccess2NullCheckAccessorConverter extends FieldAccess2AccessorConverter {

  public FieldAccess2NullCheckAccessorConverter(
      IndentPrinter printer, boolean printComments,
      Collection<ASTExpression> ignore
  ) {
    super(printer, printComments, ignore);
  }

  @Override
  public void handle(ASTFieldAccessExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getExpression().accept(getTraverser());

    getPrinter().stripTrailing();
    getPrinter().print("?.");

    if (ignore.contains(node)) {
      getPrinter().print(node.getName() + " ");
    }
    else {
      getPrinter().print(attr2Getter(node) + " ");
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }

  }

}
