package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Collection;

public class Name2MutatorConverter extends Name2AccessorConverter {

  protected SharedBoolean lastFieldAccess;

  public Name2MutatorConverter(
      IndentPrinter printer,
      boolean printComments,
      Collection<ASTExpression> ignore,
      SharedBoolean lastFieldAccess
  ) {
    super(printer, printComments, ignore);
    this.lastFieldAccess = lastFieldAccess;
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
      if (lastFieldAccess.getValue()) {
        getPrinter().print("this." + ExpressionConversion.getInstance().attr2Setter(node) + " ");
        lastFieldAccess.setValue(false);
      }
      else {
        getPrinter().print("this." + ExpressionConversion.getInstance().attr2Getter(node) + " ");
      }
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

}
