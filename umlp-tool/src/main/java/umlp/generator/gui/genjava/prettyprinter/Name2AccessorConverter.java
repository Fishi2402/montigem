package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.List;
import java.util.function.Function;

public class Name2AccessorConverter extends ExpressionsBasisPrettyPrinter {

  protected final List<ASTExpression> ignore;

  public Name2AccessorConverter(
      IndentPrinter printer,
      boolean printComments,
      List<ASTExpression> ignore
  ) {
    super(printer, printComments);
    this.ignore = ignore;
  }

  @Override
  public void handle(ASTNameExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    if (ignore.contains(node)) {
      getPrinter().print("this." + node.getName() + " ");
    }
    else {
      getPrinter().print("this." + ExpressionConversion.getInstance().attr2Getter(node) + " ");
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

}
