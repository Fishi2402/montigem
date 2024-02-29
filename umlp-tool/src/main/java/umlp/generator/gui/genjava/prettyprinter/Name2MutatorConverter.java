package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.Collection;

import static umlp.generator.gui.genjava.prettyprinter.Expression2SetterPrinter.SETTER_PARAM;

public class Name2MutatorConverter extends Name2AccessorConverter {

  protected SharedBoolean lastFieldAccess;

  public Name2MutatorConverter(
      IndentPrinter printer, boolean printComments,
      Collection<ASTExpression> ignore, Collection<ASTExpression> nonClassMembers,
      SharedBoolean lastFieldAccess
  ) {
    super(printer, printComments, ignore, nonClassMembers);
    this.lastFieldAccess = lastFieldAccess;
  }

  @Override
  public void handle(ASTNameExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    if (!nonClassMembers.contains(node)) {
      getPrinter().print("this.");
    }
    if (ignore.contains(node)) {
      getPrinter().print(node.getName() + " ");
    }
    else {
      if (lastFieldAccess.getValue()) {
        getPrinter().print(attr2Setter(node) + " ");
        lastFieldAccess.setValue(false);
      }
      else {
        getPrinter().print(attr2Getter(node) + " ");
      }
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public String attr2Setter(ASTNameExpression node) {
    return "set" + StringTransformations.capitalize(node.getName()) + "(" + SETTER_PARAM + ")";
  }

  @Override
  public void handle(ASTLiteralExpression node) {
    errorIfExpressionIsFirst(node);
  }

  protected void errorIfExpressionIsFirst(ASTExpression node) {
    if (lastFieldAccess.getValue()) {
      Log.error("0x0000D Expected NameExpression but got '"
          + GUIGeneratorUtil.getInstance().print(node)
          + "'. " + this.getClass().getSimpleName() + " cannot produce setter.");
    }
  }

}
